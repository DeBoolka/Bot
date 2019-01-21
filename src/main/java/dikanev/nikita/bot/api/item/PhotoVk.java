package dikanev.nikita.bot.api.item;

import java.util.Objects;

public class PhotoVk {

    public int id;

    public int coreId;

    public int ownerId;

    public String accessKey = null;

    public PhotoVk() {
    }

    public PhotoVk(int id, int ownerId) {
        this.id = id;
        this.ownerId = ownerId;
    }

    public PhotoVk(int id, int ownerId, int coreId) {
        this.id = id;
        this.coreId = coreId;
        this.ownerId = ownerId;
    }

    public PhotoVk(String vkAndOwner, int coreId) {
        String[] vk = vkAndOwner.split("_");
        ownerId = Integer.valueOf(vk[0]);
        if (vk.length > 1) {
            id = Integer.valueOf(vk[1]);
        }
        if (vk.length > 2) {
            accessKey = vk[2];
        }

        this.coreId = coreId;
    }

    public PhotoVk(int id, int ownerId, String accessKey, int coreId) {
        this.id = id;
        this.coreId = coreId;
        this.ownerId = ownerId;
        this.accessKey = accessKey;
    }

    public String getConcatId(){
        //todo: сделать
        return (ownerId < 0 ? "-" : "") + ownerId + "_" + id + (accessKey != null ? "_" + accessKey : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoVk photoVk = (PhotoVk) o;
        return id == photoVk.id &&
                coreId == photoVk.coreId &&
                ownerId == photoVk.ownerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, coreId, ownerId);
    }

    @Override
    public String toString() {
        return "PhotoVk{" +
                "id=" + id +
                ", coreId=" + coreId +
                ", ownerId=" + ownerId +
                ", accessKey=" + accessKey +
                '}';
    }
}
