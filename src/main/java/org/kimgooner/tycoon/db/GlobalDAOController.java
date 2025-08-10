package org.kimgooner.tycoon.db;

import org.kimgooner.tycoon.db.dao.*;
import org.kimgooner.tycoon.db.dao.combat.CombatDAO;
import org.kimgooner.tycoon.db.dao.farming.FarmingDAO;
import org.kimgooner.tycoon.db.dao.fishing.FishingDAO;
import org.kimgooner.tycoon.db.dao.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.mining.MiningDAO;

import java.sql.Connection;

public class GlobalDAOController {
    private final Connection conn;

    private final MemberDAO memberDAO;

    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;


    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    private final DataStorageDAO dataStorageDAO;


    public GlobalDAOController(Connection conn) {
        this.conn = conn;

        this.memberDAO = new MemberDAO(conn);

        this.miningDAO = new MiningDAO(conn);
        this.heartDAO = new HeartDAO(conn);

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

    public FarmingDAO getFarmingDAO() {
        return farmingDAO;
    }
    public FishingDAO getFishingDAO() {
        return fishingDAO;
    }
    public CombatDAO getCombatDAO() {
        return combatDAO;
    }

    public DataStorageDAO getDataStorageDAO() {
        return dataStorageDAO;
    }
}
