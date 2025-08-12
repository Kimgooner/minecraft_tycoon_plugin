package org.kimgooner.tycoon.db;

import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.db.dao.MemberDAO;
import org.kimgooner.tycoon.db.dao.job.combat.CombatDAO;
import org.kimgooner.tycoon.db.dao.job.farming.FarmingDAO;
import org.kimgooner.tycoon.db.dao.job.fishing.FishingDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;

public class GlobalDAOController {
    private final JavaPlugin plugin;

    private final MemberDAO memberDAO;

    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;


    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    private final DataStorageDAO dataStorageDAO;


    public GlobalDAOController(DatabaseManager databaseManager, JavaPlugin plugin) {
        this.plugin = plugin;

        this.memberDAO = new MemberDAO(databaseManager);

        this.miningDAO = new MiningDAO(databaseManager, plugin,this);
        this.heartDAO = new HeartDAO(databaseManager);
        this.heartInfoDAO = new HeartInfoDAO(databaseManager, plugin);

        this.farmingDAO = new FarmingDAO(databaseManager);
        this.fishingDAO = new FishingDAO(databaseManager);
        this.combatDAO = new CombatDAO(databaseManager);

        this.dataStorageDAO = new DataStorageDAO(databaseManager);
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
