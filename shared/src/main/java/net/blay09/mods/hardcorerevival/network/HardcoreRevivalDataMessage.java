package net.blay09.mods.hardcorerevival.network;

import net.blay09.mods.hardcorerevival.HardcoreRevival;
import net.blay09.mods.hardcorerevival.capability.HardcoreRevivalData;
import net.blay09.mods.hardcorerevival.client.HardcoreRevivalClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HardcoreRevivalDataMessage {
    private final int entityId;
    private final boolean knockedOut;
    private final int knockoutTicksPassed;
    private final boolean beingRescued;

    public HardcoreRevivalDataMessage(int entityId, boolean knockedOut, int knockoutTicksPassed, boolean beingRescued) {
        this.entityId = entityId;
        this.knockedOut = knockedOut;
        this.knockoutTicksPassed = knockoutTicksPassed;
        this.beingRescued = beingRescued;
    }

    public static void encode(HardcoreRevivalDataMessage message, FriendlyByteBuf buf) {
        int initialWriterIndex = buf.writerIndex();
    
        // Attempt to write data to the buffer
        try {
            buf.writeInt(message.entityId);
            buf.writeBoolean(message.knockedOut);
            buf.writeInt(message.knockoutTicksPassed);
            buf.writeBoolean(message.beingRescued);
        } catch (IndexOutOfBoundsException e) {
            int requiredCapacity = initialWriterIndex + 4 * Integer.BYTES + 2 * Byte.BYTES;
    
            buf.capacity(requiredCapacity);
    
            buf.writerIndex(initialWriterIndex);
            buf.writeInt(message.entityId);
            buf.writeBoolean(message.knockedOut);
            buf.writeInt(message.knockoutTicksPassed);
            buf.writeBoolean(message.beingRescued);
        }
    }

    public static HardcoreRevivalDataMessage decode(FriendlyByteBuf buf) {
    try {
        if (buf.readableBytes() < 4 * Integer.BYTES + 2 * Byte.BYTES) {
            throw new IllegalStateException("Not enough data to read");
        }

        int entityId = buf.readInt();
        boolean knockedOut = buf.readBoolean();
        int knockoutTicksPassed = buf.readInt();
        boolean beingRescued = buf.readBoolean();

        return new HardcoreRevivalDataMessage(entityId, knockedOut, knockoutTicksPassed, beingRescued);
    } catch (Exception e) {
        e.printStackTrace(); // Print the exception for debugging purposes

        throw new IllegalStateException("Error decoding HardcoreRevivalDataMessage", e);
    }
}

    public static void handle(Player player, HardcoreRevivalDataMessage message) {
        if (player != null) {
            Entity entity = player.level().getEntity(message.entityId);
            if (entity != null) {
                HardcoreRevivalData revivalData = entity.getId() == player.getId() ? HardcoreRevival.getClientRevivalData() : HardcoreRevival.getRevivalData(entity);
                revivalData.setKnockedOut(message.knockedOut);
                revivalData.setKnockoutTicksPassed(message.knockoutTicksPassed);
                HardcoreRevivalClient.setBeingRescued(message.beingRescued);
            }
        }
    }
}
