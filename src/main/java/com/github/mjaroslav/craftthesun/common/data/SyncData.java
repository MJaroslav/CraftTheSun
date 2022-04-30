package com.github.mjaroslav.craftthesun.common.data;

import com.github.mjaroslav.craftthesun.common.network.packet.S00SyncData;
import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

@Data
@NoArgsConstructor
public final class SyncData {
    public static final String TAG_SYNC_DATA = "sync_data";
    public static final String TAG_TYPE = "type";
    public static final String TAG_HUMANITY = "humanity";
    private boolean changed = false;

    @NotNull
    private PlayerType type = PlayerType.HUMAN;
    @Range(from = 0, to = Integer.MAX_VALUE)
    private int humanity;

    public void setType(@NotNull PlayerType type) {
        this.type = type;
        changed = true;
    }

    public void setHumanity(@Range(from = 0, to = Integer.MAX_VALUE) int humanity) {
        this.humanity = humanity;
        changed = true;
    }

    public void loadFromNBT(@NotNull NBTTagCompound compound) {
        val dataTag = compound.getCompoundTag(TAG_SYNC_DATA);
        type = PlayerType.getById(dataTag.getInteger(TAG_TYPE));
        humanity = dataTag.getInteger(TAG_HUMANITY);
    }

    public void saveToNBT(@NotNull NBTTagCompound compound) {
        val dataTag = new NBTTagCompound();
        dataTag.setInteger(TAG_TYPE, type.ordinal());
        dataTag.setInteger(TAG_HUMANITY, humanity);
        compound.setTag(TAG_SYNC_DATA, dataTag);
    }

    public S00SyncData getSyncPacket() {
        val result = new S00SyncData();
        val data = new NBTTagCompound();
        saveToNBT(data);
        result.setData(data);
        return result;
    }

    public void handleSyncPacket(@NotNull S00SyncData packet) {
        loadFromNBT(packet.getData());
    }

    @RequiredArgsConstructor
    @Getter
    public enum PlayerType {
        HUMAN(false), CURSED(false), HOLLOW(true), UNDEAD_HUMAN(true);

        private final boolean undead;

        public static PlayerType getById(@Range(from = 0, to = 2) int ordinal) {
            for (var type : values())
                if (type.ordinal() == ordinal)
                    return type;
            return HUMAN;
        }
    }
}
