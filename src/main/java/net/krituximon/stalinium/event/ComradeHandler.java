package net.krituximon.stalinium.event;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.krituximon.stalinium.Stalinium;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(
        modid = Stalinium.MODID,
        bus   = EventBusSubscriber.Bus.GAME
)
public class ComradeHandler {
    public static final Map<UUID, Party> PARTIES = new ConcurrentHashMap<>();
    private static final Map<UUID, UUID> PENDING_INVITES = new ConcurrentHashMap<>();

    public static class Party {
        public final UUID leader;
        public final Set<UUID> members = ConcurrentHashMap.newKeySet();
        public Party(UUID leader) {
            this.leader = leader;
            this.members.add(leader);
        }
        public boolean isMember(UUID u) { return members.contains(u); }
        public boolean isLeader(UUID u) { return leader.equals(u); }
        public void addMember(UUID u)    { members.add(u); }
        public void removeMember(UUID u) { members.remove(u); }
    }
    
    public static Optional<Party> findPartyOf(UUID uuid) {
        return PARTIES.values().stream()
                .filter(p -> p.isMember(uuid))
                .findAny();
    }
    
    private static Optional<Party> findPartyByLeader(UUID leader) {
        return Optional.ofNullable(PARTIES.get(leader));
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("comrade")
                        .then(Commands.literal("party")
                                .then(Commands.literal("create")
                                        .executes(ctx -> {
                                            ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                            UUID me = sender.getUUID();
                                            if (findPartyOf(me).isPresent()) {
                                                ctx.getSource().sendFailure(Component.literal("§eYou are already in a party."));
                                                return 0;
                                            }
                                            PARTIES.put(me, new Party(me));
                                            ctx.getSource().sendSuccess(() -> Component.literal("§aParty created. You are now the leader."), false);
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("invite")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> {
                                                    ServerPlayer leader = ctx.getSource().getPlayerOrException();
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                    UUID lid = leader.getUUID(), tid = target.getUUID();
                                                    if (lid.equals(tid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eYou cannot invite yourself."));
                                                        return 0;
                                                    }
                                                    Party party = findPartyOf(lid).orElseGet(() -> {
                                                        Party auto = new Party(lid);
                                                        PARTIES.put(lid, auto);
                                                        ctx.getSource().sendSuccess(() -> Component.literal("§aNo existing party—one was created for you."), false);
                                                        return auto;
                                                    });
                                                    if (!party.isLeader(lid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eOnly the party leader can invite."));
                                                        return 0;
                                                    }
                                                    if (party.isMember(tid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eThat player is already in your party."));
                                                        return 0;
                                                    }
                                                    PENDING_INVITES.put(tid, lid);
                                                    target.sendSystemMessage(Component.literal(
                                                            "§6" + leader.getName().getString()
                                                                    + " §awants you to join their party! Type §e/comrade party accept "
                                                                    + leader.getName().getString() + "§a."
                                                    ));
                                                    ctx.getSource().sendSuccess(() ->
                                                            Component.literal("§aInvitation sent to §6" + target.getName().getString()), false);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("accept")
                                        .then(Commands.argument("leader", EntityArgument.player())
                                                .executes(ctx -> {
                                                    ServerPlayer you    = ctx.getSource().getPlayerOrException();
                                                    ServerPlayer leader= EntityArgument.getPlayer(ctx,"leader");
                                                    UUID yid = you.getUUID(), lid = leader.getUUID();
                                                    if (!Objects.equals(PENDING_INVITES.get(yid), lid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eNo invitation found from that leader."));
                                                        return 0;
                                                    }
                                                    Party party = findPartyByLeader(lid)
                                                            .orElseGet(() -> {
                                                                Party p = new Party(lid);
                                                                PARTIES.put(lid, p);
                                                                return p;
                                                            });
                                                    party.addMember(yid);
                                                    PENDING_INVITES.remove(yid);
                                                    you.sendSystemMessage(Component.literal("§aYou joined §6" + leader.getName().getString() + "§a’s party."));
                                                    leader.sendSystemMessage(Component.literal("§a" + you.getName().getString() + " §ais now your comrade."));
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> {
                                                    ServerPlayer leader = ctx.getSource().getPlayerOrException();
                                                    ServerPlayer target = EntityArgument.getPlayer(ctx,"player");
                                                    UUID lid = leader.getUUID(), tid = target.getUUID();
                                                    Optional<Party> opt = findPartyOf(lid);
                                                    if (opt.isEmpty()) {
                                                        ctx.getSource().sendFailure(Component.literal("§eYou have no party."));
                                                        return 0;
                                                    }
                                                    Party party = opt.get();
                                                    if (!party.isLeader(lid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eOnly the leader can remove."));
                                                        return 0;
                                                    }
                                                    if (!party.isMember(tid)) {
                                                        ctx.getSource().sendFailure(Component.literal("§eThat player is not in your party."));
                                                        return 0;
                                                    }
                                                    party.removeMember(tid);
                                                    target.sendSystemMessage(Component.literal(
                                                            "§cYou have been removed from §6" + leader.getName().getString() + "§c’s party."
                                                    ));
                                                    ctx.getSource().sendSuccess(() ->
                                                            Component.literal("§aRemoved “" + target.getName().getString() + "”."), false);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("leave")
                                        .executes(ctx -> {
                                            ServerPlayer you = ctx.getSource().getPlayerOrException();
                                            UUID youId = you.getUUID();
                                            Optional<Party> opt = findPartyOf(youId);
                                            if (opt.isEmpty()) {
                                                ctx.getSource().sendFailure(Component.literal("§eYou are not in a party."));
                                                return 0;
                                            }
                                            Party party = opt.get();
                                            if (party.isLeader(youId)) {
                                                ctx.getSource().sendFailure(Component.literal(
                                                        "§eYou are the leader — use /comrade party disband to tear it down."));
                                                return 0;
                                            }
                                            party.removeMember(youId);
                                            you.sendSystemMessage(Component.literal("§cYou left the party."));
                                            MinecraftServer srv = ctx.getSource().getServer();
                                            ServerPlayer leader = srv.getPlayerList().getPlayer(party.leader);
                                            if (leader != null) {
                                                leader.sendSystemMessage(Component.literal(
                                                        "§e" + you.getName().getString() + " has left your party."));
                                            }
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("disband")
                                        .executes(ctx -> {
                                            ServerPlayer leader = ctx.getSource().getPlayerOrException();
                                            UUID lid = leader.getUUID();
                                            Optional<Party> opt = findPartyByLeader(lid);
                                            if (opt.isEmpty()) {
                                                ctx.getSource().sendFailure(Component.literal("§eYou are not leading any party."));
                                                return 0;
                                            }
                                            Party party = opt.get();
                                            MinecraftServer srv = ctx.getSource().getServer();
                                            party.members.forEach(m -> {
                                                ServerPlayer p = srv.getPlayerList().getPlayer(m);
                                                if (p != null) {
                                                    p.sendSystemMessage(Component.literal("§cYour party has been disbanded."));
                                                }
                                            });
                                            PENDING_INVITES.entrySet().removeIf(e -> e.getValue().equals(lid));
                                            PARTIES.remove(lid);

                                            return 1;
                                        })
                                )
                                .then(Commands.literal("list")
                                        .executes(ctx -> {
                                            ServerPlayer you = ctx.getSource().getPlayerOrException();
                                            UUID youId = you.getUUID();
                                            Optional<Party> opt = findPartyOf(youId);
                                            if (opt.isEmpty()) {
                                                ctx.getSource().sendSuccess(() -> Component.literal("§eYou are not in a party."), false);
                                                return 0;
                                            }
                                            Party party = opt.get();
                                            MinecraftServer srv = ctx.getSource().getServer();
                                            List<String> names = new ArrayList<>();
                                            for(UUID m : party.members){
                                                ServerPlayer p = srv.getPlayerList().getPlayer(m);
                                                names.add(p != null ? p.getName().getString() : m.toString());
                                            }
                                            String out = String.join(", ", names);
                                            ctx.getSource().sendSuccess(() -> Component.literal("§6Party: §f" + out), false);
                                            return 1;
                                        })
                                )
                        )
        );
    }

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        LivingEntity src = event.getEntity();
        if (src.level().isClientSide || !(src instanceof Player)) return;
        if (!(event.getTarget() instanceof ServerPlayer victim)) return;
        UUID a = src.getUUID(), b = victim.getUUID();
        Optional<Party> opt = findPartyOf(a);
        if (opt.isPresent() && opt.get().isMember(b)) {
            event.setCanceled(true);
            src.sendSystemMessage(Component.literal(
                    "§cYou cannot hurt your comrade “" + victim.getName().getString() + "”!"
            ));
        }
    }
}