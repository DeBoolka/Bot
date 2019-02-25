package dikanev.nikita.bot.logic.connector.db;

import com.google.common.base.Joiner;
import dikanev.nikita.bot.api.item.PhotoVk;
import dikanev.nikita.bot.service.client.SQLRequest;
import dikanev.nikita.bot.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhotoDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoDBConnector.class);

    public static void addPhoto(List<PhotoVk> photoInVkAndCore) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO img(id_vk, id_core) VALUES ");
        boolean[] isFirstValues = new boolean[]{true};
        photoInVkAndCore.forEach(it -> {
            if (!isFirstValues[0]) {
                sql.append(", ");
            } else {
                isFirstValues[0] = false;
            }

            sql.append("('").append(escape(it.getConcatId())).append("', ").append(it.coreId).append(")");
        });

        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql.toString());
        req.executeUpdate();
        req.close();
    }

    public static List<PhotoVk> getPhotoFromCore(Integer[] photoCore) throws SQLException {
        if (photoCore == null || photoCore.length == 0) {
            return new ArrayList<>();
        }

        String sql = "SELECT id_core, id_vk FROM img WHERE id_core IN (" + Joiner.on(", ").join(photoCore) + ")";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);

        ResultSet res = req.executeQuery();
        List<PhotoVk> photos = new ArrayList<>();
        while (res.next()) {
            photos.add(new PhotoVk(res.getString("id_vk"), res.getInt("id_core")));
        }

        res.close();
        return photos;
    }

    public static boolean deletePhoto(Integer[] photos) throws SQLException {
        if (photos == null || photos.length == 0) {
            return true;
        }

        String sql = "DELETE FROM img WHERE id_core IN (" + Joiner.on(", ").join(photos) + ")";
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection()).build(sql);

        int res = req.executeUpdate();
        req.close();
        return res > 0;
    }

    private static String escape(String it) {
        try {
            return URLEncoder.encode(it, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Failed escape word.", e);
        }
        return it;
    }
}
