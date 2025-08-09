package org.kimgooner.tycoon.db;

import org.kimgooner.tycoon.db.dao.*;

import java.sql.Connection;

public class DAOController {
    private final Connection conn;

    private final MemberDAO memberDAO;
    private final MiningDAO miningDAO;
    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    private final DataStorageDAO dataStorageDAO;

    public DAOController(Connection conn) {
        this.conn = conn;

        this.memberDAO = new MemberDAO(conn);
        this.miningDAO = new MiningDAO(conn);
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
