package org.kimgooner.tycoon.db;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.db.dao.MemberDAO;
import org.kimgooner.tycoon.db.dao.combat.CombatDAO;
import org.kimgooner.tycoon.db.dao.farming.FarmingDAO;
import org.kimgooner.tycoon.db.dao.fishing.FishingDAO;
import org.kimgooner.tycoon.db.dao.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.mining.MiningDAO;

import java.sql.Connection;

public class GlobalDAOController {
    private final Connection conn;
    private final JavaPlugin plugin;

    private final MemberDAO memberDAO;

    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;


    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    private final DataStorageDAO dataStorageDAO;


    public GlobalDAOController(Connection conn, JavaPlugin plugin) {
        this.conn = conn;
        this.plugin = plugin;

        this.memberDAO = new MemberDAO(conn);

        this.miningDAO = new MiningDAO(conn, plugin,this);
        this.heartDAO = new HeartDAO(conn);
        this.heartInfoDAO = new HeartInfoDAO(conn);

        this.farmingDAO = new FarmingDAO(conn);
        this.fishingDAO = new FishingDAO(conn);
        this.combatDAO = new CombatDAO(conn);

        this.dataStorageDAO = new DataStorageDAO(conn);
    }

    public MemberDAO getMemberDAO() {
        return memberDAO;
    }

    public MiningDAO getMiningDAO() {
        return miningDAO;
    }
    public HeartDAO getHeartDAO() { return heartDAO; }
    public HeartInfoDAO getHeartInfoDAO() { return heartInfoDAO; }

    public FarmingDAO getFarmingDAO() {
        return farmingDAO;
    }
    public FishingDAO getFishingDAO() {
        return fishingDAO;
    }
    public CombatDAO getCombatDAO() {
        return combatDAO;
    }

    public DataStorageDAO getDataStorageDAO() {return dataStorageDAO;}
}
