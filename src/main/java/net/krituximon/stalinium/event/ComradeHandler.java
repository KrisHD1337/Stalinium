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

/**
 * Party/”comrade” handler.  
 *
 * Commands:
 *   /comrade party create
 *   /comrade invite <player>
 *   /comrade accept <leader>
 *   /comrade remove <member>
 *   /comrade list
 *
 * Only the party “leader” can remove others.  Invites must be accepted by the target.  
 * If you invite someone but you aren’t yet in a party, you automatically become leader of a new party.
 */
@EventBusSubscriber(
        modid = Stalinium.MODID,
        bus   = EventBusSubscriber.Bus.GAME
)
public class ComradeHandler {
    /** Maps each party leader → Party object.  Only the leader’s UUID is the key. */
    private static final Map<UUID, Party> PARTIES = new ConcurrentHashMap<>();

    /** If player X has been invited by leader Y, pendingInvites maps X → Y. */
    private static final Map<UUID, UUID> PENDING_INVITES = new ConcurrentHashMap<>();

    /** A small helper class to store party data. */
    private static class Party {
        final UUID leader;
        final Set<UUID> members = ConcurrentHashMap.newKeySet();

        Party(UUID leader) {
            this.leader = leader;
            this.members.add(leader);
        }

        boolean isMember(UUID uuid) {
            return members.contains(uuid);
        }

        void addMember(UUID uuid) {
            members.add(uuid);
        }

        void removeMember(UUID uuid) {
            members.remove(uuid);
        }

        boolean isLeader(UUID uuid) {
            return leader.equals(uuid);
        }
    }
    
    private static Optional<Party> findPartyOf(UUID uuid) {
        return PARTIES.values().stream()
                .filter(p -> p.isMember(uuid))
                .findAny();
    }
    
    private static Optional<Party> findPartyByLeader(UUID leaderUuid) {
        return Optional.ofNullable(PARTIES.get(leaderUuid));
    }
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal("comrade")
                        .then(Commands.literal("party")
                                .then(Commands.literal("create")
                                        .executes(ctx -> {
                                            ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                            UUID senderId = sender.getUUID();

                                            // If sender is already in a party, do nothing.
                                            if (findPartyOf(senderId).isPresent()) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eYou are already in a party.")
                                                );
                                                return 0;
                                            }
                                            Party newParty = new Party(senderId);
                                            PARTIES.put(senderId, newParty);
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal("§aParty created. You are now the leader."),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("invite")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                            UUID senderId = sender.getUUID();
                                            UUID targetId = target.getUUID();
                                            if (senderId.equals(targetId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eYou cannot invite yourself.")
                                                );
                                                return 0;
                                            }
                                            Optional<Party> senderPartyOpt = findPartyOf(senderId);
                                            if (senderPartyOpt.isPresent()) {
                                                Party party = senderPartyOpt.get();
                                                if (party.isMember(targetId)) {
                                                    ctx.getSource().sendFailure(
                                                            Component.literal("§eThat player is already in your party.")
                                                    );
                                                    return 0;
                                                }
                                            } else {
                                                Party auto = new Party(senderId);
                                                PARTIES.put(senderId, auto);
                                                senderPartyOpt = Optional.of(auto);
                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal("§aNo party found, so one was automatically created. You are now leader."),
                                                        false
                                                );
                                            }
                                            Party party = senderPartyOpt.get();
                                            if (!party.isLeader(senderId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eOnly the party leader can invite new comrades.")
                                                );
                                                return 0;
                                            }
                                            PENDING_INVITES.put(targetId, senderId);
                                            target.sendSystemMessage(
                                                    Component.literal("§6" + sender.getName().getString()
                                                            + " §awants you to join their party. Type §e/comrade accept "
                                                            + sender.getName().getString() + " §ato accept.")
                                            );
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal("§aInvitation sent to §6" + target.getName().getString() + "§a."),
                                                    false
                                            );
                                            return 1;
                                        })
                                )
                        )
                        // /comrade accept <leader>
                        .then(Commands.literal("accept")
                                .then(Commands.argument("leader", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer accepter = ctx.getSource().getPlayerOrException();
                                            ServerPlayer leader = EntityArgument.getPlayer(ctx, "leader");
                                            UUID accepterId = accepter.getUUID();
                                            UUID leaderId = leader.getUUID();
                                            UUID expected = PENDING_INVITES.get(accepterId);
                                            if (expected == null || !expected.equals(leaderId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eNo invitation found from that leader.")
                                                );
                                                return 0;
                                            }

                                            // Find (or create) the leader’s party:
                                            Party party = findPartyByLeader(leaderId)
                                                    .orElseGet(() -> {
                                                        Party brandNew = new Party(leaderId);
                                                        PARTIES.put(leaderId, brandNew);
                                                        return brandNew;
                                                    });

                                            // Add this player to that party:
                                            party.addMember(accepterId);
                                            PENDING_INVITES.remove(accepterId);

                                            accepter.sendSystemMessage(
                                                    Component.literal("§aYou have joined §6" + leader.getName().getString() + "§a’s party.")
                                            );
                                            leader.sendSystemMessage(
                                                    Component.literal("§a" + accepter.getName().getString() + " §ais now your comrade.")
                                            );
                                            return 1;
                                        })
                                )
                        )
                        // /comrade remove <player>
                        .then(Commands.literal("remove")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> {
                                            ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                            ServerPlayer toRemove = EntityArgument.getPlayer(ctx, "player");
                                            UUID senderId   = sender.getUUID();
                                            UUID removeId   = toRemove.getUUID();
                                            Optional<Party> maybeParty = findPartyOf(senderId);
                                            if (maybeParty.isEmpty()) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eYou are not comrades with anyone.")
                                                );
                                                return 0;
                                            }
                                            Party party = maybeParty.get();
                                            if (!party.isLeader(senderId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eOnly the party leader may remove members.")
                                                );
                                                return 0;
                                            }
                                            if (removeId.equals(senderId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eYou cannot remove yourself from your own party.")
                                                );
                                                return 0;
                                            }
                                            if (!party.isMember(removeId)) {
                                                ctx.getSource().sendFailure(
                                                        Component.literal("§eThat player is not one of your comrades.")
                                                );
                                                return 0;
                                            }

                                            party.removeMember(removeId);
                                            toRemove.sendSystemMessage(
                                                    Component.literal("§cYou are no longer "
                                                            + sender.getName().getString() + "'s comrade.")
                                            );
                                            ctx.getSource().sendSuccess(
                                                    () -> Component.literal("§a“" + toRemove.getName().getString()
                                                            + "” is no longer your comrade."),
                                                    false
                                            );
                                            if (party.members.size() == 1 && party.isLeader(senderId)) {
                                                PARTIES.remove(senderId);
                                                sender.sendSystemMessage(
                                                        Component.literal("§6Your party was disbanded (no more members).")
                                                );
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    ServerPlayer sender = ctx.getSource().getPlayerOrException();
                                    UUID senderId = sender.getUUID();
                                    Optional<Party> maybeParty = findPartyOf(senderId);
                                    if (maybeParty.isEmpty()) {
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("§eYou are not in a party."),
                                                false
                                        );
                                        return 0;
                                    }
                                    Party party = maybeParty.get();
                                    MinecraftServer server = ctx.getSource().getServer();
                                    List<String> displayNames = new ArrayList<>();
                                    for (UUID memberUuid : party.members) {
                                        ServerPlayer online = server.getPlayerList().getPlayer(memberUuid);
                                        if (online != null) {
                                            displayNames.add(online.getName().getString());
                                        } else {
                                            displayNames.add(memberUuid.toString());
                                        }
                                    }
                                    String joined = String.join(", ", displayNames);
                                    ctx.getSource().sendSuccess(
                                            () -> Component.literal("§6Party members: §f" + joined),
                                            false
                                    );
                                    return 1;
                                })
                        )
        );
    }
    
    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event) {
        LivingEntity source = event.getEntity();
        if (source.level().isClientSide) return;
        if (!(source instanceof Player)) return;

        if (event.getTarget() instanceof ServerPlayer targetPlayer) {
            UUID sourceId = source.getUUID();
            UUID targetId = targetPlayer.getUUID();

            // If both are in the same party, cancel damage:
            Optional<Party> maybeParty = findPartyOf(sourceId);
            if (maybeParty.isPresent()) {
                Party party = maybeParty.get();
                if (party.isMember(targetId)) {
                    event.setCanceled(true);
                    source.sendSystemMessage(
                            Component.literal("§cYou cannot hurt your comrade “"
                                    + targetPlayer.getName().getString() + "”!")
                    );
                }
            }
        }
    }
}