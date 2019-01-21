package dikanev.nikita.bot.controller;

import dikanev.nikita.bot.api.exceptions.ApiException;
import dikanev.nikita.bot.api.item.Ammunition;
import dikanev.nikita.bot.logic.connector.core.AmmunitionCoreConnector;
import dikanev.nikita.bot.logic.connector.db.users.UserDBConnector;

import java.sql.SQLException;
import java.util.List;

public class AmmunitionController {
    public static Ammunition addAmmunition(String token, int userId, String name, String[] photoLinks) throws ApiException, SQLException {
        return AmmunitionCoreConnector.addAmmunition(token, UserDBConnector.getIdCore(userId), name, photoLinks);
    }

    public static List<Ammunition> getAmmunitionByUser(String token, int userId, int indent, int count) throws ApiException, SQLException {
        return AmmunitionCoreConnector.getAmmunition(token, UserDBConnector.getIdCore(userId), indent, count);
    }

    public static boolean deleteAmmunition(String token, int ammunitionId) throws ApiException {
        return AmmunitionCoreConnector.deleteAmmunition(token, ammunitionId);
    }
}
