package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.api.IslandCreateEvent;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.api.IslandWorthCalculatedEvent;
import com.iridium.iridiumskyblock.api.MissionCompleteEvent;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.support.SpawnerSupport;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Island {

    public static class Warp {
        Location location;
        String name;
        String password;

        public Warp(Location location, String name, String password) {
            this.location = location;
            this.name = name;
            this.password = password;
        }

        public Location getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }
    }

    @Getter
    private String owner;
    @Getter
    private Set<String> members;
    @Getter
    private Location pos1;
    @Getter
    private Location pos2;
    @Getter
    private Location center;
    @Getter
    @Setter
    private Location home;

    @Getter
    private transient UpgradeGUI upgradeGUI;
    @Getter
    private transient BoosterGUI boosterGUI;
    @Getter
    private transient MissionsGUI missionsGUI;
    @Getter
    private transient MembersGUI membersGUI;
    @Getter
    private transient WarpGUI warpGUI;
    @Getter
    private transient BorderColorGUI borderColorGUI;
    @Getter
    private transient SchematicSelectGUI schematicSelectGUI;
    @Getter
    private transient PermissionsGUI permissionsGUI;
    @Getter
    private transient IslandMenuGUI islandMenuGUI;
    @Getter
    private transient IslandAdminGUI islandAdminGUI;
    @Getter
    private transient CoopGUI coopGUI;
    @Getter
    private transient BankGUI bankGUI;
    @Getter
    private transient BiomeGUI biomeGUI;
    @Getter
    private transient BiomeGUI netherBiomeGUI;
    @Getter
    private transient VisitorGUI visitorGUI;

    @Getter
    private int id;

    @Getter
    @Setter
    private int spawnerBooster;
    @Getter
    @Setter
    private int farmingBooster;
    @Getter
    @Setter
    private int expBooster;
    @Getter
    @Setter
    private int flightBooster;

    private transient int boosterid;

    @Getter
    @Setter
    private int crystals;

    @Getter
    private int sizeLevel;
    @Getter
    @Setter
    private int memberLevel;
    @Getter
    @Setter
    private int warpLevel;
    @Getter
    @Setter
    private int oreLevel;

    public transient int generateID;

    @Getter
    private double value;

    private double lastMissionValue;

    @Getter
    private double extravalue;

    public transient ConcurrentHashMap<String, Integer> valuableBlocks;
    public transient ConcurrentHashMap<String, Integer> spawners;
    public ConcurrentHashMap<Location, Integer> stackedBlocks;

    @Getter
    private final List<Warp> warps;

    private double startvalue;

    private Map<String, Integer> missions = new HashMap<>();

    private Map<String, Integer> missionLevels = new HashMap<>();

    @Getter
    @Setter
    private boolean visit;

    @Getter
    private Color borderColor;

    private Map<Role, Permissions> permissions;

    @Getter
    @Setter
    private String schematic;
    @Getter
    @Setter
    private String netherschematic;

    private Set<String> bans;

    private Set<String> votes;

    private Set<Integer> coop;

    public transient Set<Integer> coopInvites;

    @Setter
    private String name;

    public double money;
    public int exp;

    @Getter
    private XBiome biome;

    @Getter
    private XBiome netherBiome;

    public transient Set<Location> failedGenerators;
    public transient int interestCrystal;
    public transient int interestExp;
    public transient double interestMoney;

    private Date lastRegen;

    private transient Set<Player> playersOnIsland;
    private long lastPlayerCaching;

    private static final transient boolean ISFLAT = XMaterial.supports(13);
    private static transient Method getMaterial;
    private static transient Method getBlock;

    static {
        try {
            getMaterial = Material.class.getMethod("getMaterial", int.class);
            getBlock = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (NoSuchMethodException e) {
            getMaterial = null;
            getBlock = null;
        }
    }

    public Island(Player owner, Location pos1, Location pos2, Location center, Location home, int id) {
        User user = User.getUser(owner);
        user.role = Role.Owner;
        this.biome = IridiumSkyblock.getConfiguration().defaultBiome;
        this.netherBiome = XBiome.NETHER_WASTES;
        valuableBlocks = new ConcurrentHashMap<>();
        spawners = new ConcurrentHashMap<>();
        this.owner = user.player;
        this.name = user.name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.center = center;
        this.home = home;
        this.members = new HashSet<>(Collections.singletonList(user.player));
        this.id = id;
        spawnerBooster = 0;
        farmingBooster = 0;
        expBooster = 0;
        flightBooster = 0;
        crystals = 0;
        sizeLevel = 1;
        memberLevel = 1;
        warpLevel = 1;
        oreLevel = 1;
        value = 0;
        lastMissionValue = 0;
        warps = new ArrayList<>();
        startvalue = -1;
        borderColor = IridiumSkyblock.border.startingColor;
        visit = IridiumSkyblock.getConfiguration().defaultIslandPublic;
        permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        this.coop = new HashSet<>();
        this.bans = new HashSet<>();
        this.votes = new HashSet<>();
        init();
        Bukkit.getPluginManager().callEvent(new IslandCreateEvent(owner, this));
    }

    public void initBlocks() {
        if (!getCenter().getWorld().isChunkLoaded(getCenter().getBlockX() >> 4, getCenter().getBlockZ() >> 4)) return;
        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        final boolean nether = IridiumSkyblock.getConfiguration().netherIslands;

        int minx = pos1.getChunk().getX();
        int minz = pos1.getChunk().getZ();
        int maxx = pos2.getChunk().getX();
        int maxz = pos2.getChunk().getZ();

        valuableBlocks.clear();
        spawners.clear();

        for (Location location : stackedBlocks.keySet()) {
            XMaterial xMaterial = XMaterial.matchXMaterial(location.getBlock().getType());
            valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                if (original == null) return stackedBlocks.get(location) - 1;
                return original + (stackedBlocks.get(location) - 1);
            });
            if (xMaterial == XMaterial.AIR || xMaterial == XMaterial.CAVE_AIR || xMaterial == XMaterial.VOID_AIR) {
                stackedBlocks.remove(location);
            }
        }
        Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), (Runnable) this::sendHomograms);

        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                int finalX = x;
                int finalZ = z;

                //Update the nether world values

                if (nether) {
                    Chunk netherchunk = IslandManager.getNetherWorld().getChunkAt(x, z);
                    ChunkSnapshot nethersnapshot = netherchunk.getChunkSnapshot(true, false, false);

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        for (int x1 = 0; x1 < 16; x1++) {
                            for (int z1 = 0; z1 < 16; z1++) {
                                if (!isInIsland(x1 + (16 * finalX), z1 + (16 * finalZ)))
                                    continue;
                                final int maxy = nethersnapshot.getHighestBlockYAt(x1, z1);
                                for (int y = 0; y < maxy; y++) {
                                    final Material material;
                                    if (ISFLAT) {
                                        material = nethersnapshot.getBlockType(x1, y, z1);
                                    } else {
                                        try {
                                            material = (Material) getMaterial.invoke(null, getBlock.invoke(nethersnapshot, x1, y, z1));
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            e.printStackTrace();
                                            return;
                                        }
                                    }
                                    final XMaterial xMaterial = XMaterial.matchXMaterial(material);
                                    if (Utils.isBlockValuable(xMaterial)) {
                                        valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                                            if (original == null) return 1;
                                            return original + 1;
                                        });
                                    }
                                }
                            }
                        }
                    });
                }

                //Update overworld values

                Chunk chunk = IslandManager.getWorld().getChunkAt(x, z);
                ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, false, false);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (int x1 = 0; x1 < 16; x1++) {
                        for (int z1 = 0; z1 < 16; z1++) {
                            if (!isInIsland(x1 + (16 * finalX), z1 + (16 * finalZ)))
                                continue;
                            final int maxy = snapshot.getHighestBlockYAt(x1, z1);
                            for (int y = 0; y < maxy; y++) {
                                final Material material;
                                if (ISFLAT) {
                                    material = snapshot.getBlockType(x1, y, z1);
                                } else {
                                    try {
                                        material = (Material) getMaterial.invoke(null, getBlock.invoke(snapshot, x1, y, z1));
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                }
                                final XMaterial xMaterial = XMaterial.matchXMaterial(material);
                                if (Utils.isBlockValuable(xMaterial)) {
                                    valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                                        if (original == null) return 1;
                                        return original + 1;
                                    });
                                }
                            }
                        }
                    }
                });
            }
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), this::calculateIslandValue, 20);
    }

    public void resetMissions() {
        if (missions == null) missions = new HashMap<>();
        if (missionLevels == null) missionLevels = new HashMap<>();
        missions.clear();
        missionLevels.clear();
        lastMissionValue = value;
    }

    public int getMission(String mission) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        return missions.get(mission);
    }

    public void addMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, missions.get(mission) + amount);
        for (Missions.Mission m : IridiumSkyblock.getMissions().missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public void setMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, amount);
        for (Missions.Mission m : IridiumSkyblock.getMissions().missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public Permissions getPermissions(User user) {
        if (user.bypassing) return new Permissions();

        Role role;
        if (user.islandID == getId()) role = user.getRole();
        else if (isCoop(user.getIsland())) role = Role.Member;
        else role = Role.Visitor;
        return getPermissions(role);
    }

    public Permissions getPermissions(Role role) {
        if (permissions == null)
            permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        if (!permissions.containsKey(role)) {
            permissions.put(role, new Permissions());
        }
        return permissions.get(role);
    }

    public void setBorderColor(Color color) {
        borderColor = color;
        sendBorder();
    }

    public void sendBorder() {
        for (Player p : getPlayersOnIsland()) {
            sendBorder(p);
        }
    }

    public void sendBorder(Player p) {
        double size = IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size;
        if (size % 2 == 0) size++;
        String worldname = p.getLocation().getWorld().getName();
        if (worldname.equals(IslandManager.getWorld().getName())) {
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, getCenter());
        } else if (IridiumSkyblock.getConfiguration().netherIslands && worldname.equals(IslandManager.getNetherWorld().getName())) {
            Location loc = getCenter().clone();
            loc.setWorld(IslandManager.getNetherWorld());
            IridiumSkyblock.nms.sendWorldBorder(p, borderColor, size, loc);
        }
    }

    public void hideBorder() {
        for (Player p : getPlayersOnIsland()) {
            hideBorder(p);
        }
    }

    public void hideBorder(Player p) {
        IridiumSkyblock.nms.sendWorldBorder(p, borderColor, Integer.MAX_VALUE, getCenter().clone());
    }

    public void completeMission(String missionName) {
        missionLevels.putIfAbsent(missionName, 1);

        final Config config = IridiumSkyblock.getConfiguration();
        missions.put(missionName, (config.missionRestart == MissionRestart.Instantly ? 0 : Integer.MIN_VALUE));

        final Mission mission = IridiumSkyblock
                .getMissions()
                .missions
                .stream()
                .filter(m -> m.name.equalsIgnoreCase(missionName))
                .findAny()
                .orElse(null);
        if (mission == null) return;

        final Map<Integer, MissionData> levels = mission.levels;
        final int levelProgress = missionLevels.get(missionName);
        final MissionData level = levels.get(levelProgress);
        final int crystalReward = level.crystalReward;
        final int vaultReward = level.vaultReward;
        this.crystals += crystalReward;
        this.money += vaultReward;
        Bukkit.getPluginManager().callEvent(new MissionCompleteEvent(this, missionName, level.type, levelProgress));
        final Messages messages = IridiumSkyblock.getMessages();
        final String titleMessage = messages.missionComplete
                .replace("%mission%", missionName)
                .replace("%level%", levelProgress + "");
        final String subTitleMessage = messages.rewards
                .replace("%crystalsReward%", crystalReward + "")
                .replace("%vaultReward%", vaultReward + "");
        for (String member : members) {
            final User user = User.getUser(member);
            final Player p = Bukkit.getPlayer(user.name);
            if (p == null) continue;
            IridiumSkyblock.nms.sendTitle(p, titleMessage, 20, 40, 20);
            IridiumSkyblock.nms.sendSubTitle(p, subTitleMessage, 20, 40, 20);
        }

        //Reset current mission status
        if (mission.levels.containsKey(levelProgress + 1)) {
            //We have another mission, put us on the next level
            missions.remove(missionName);
            missionLevels.put(missionName, levelProgress + 1);
        } else if (config.missionRestart == MissionRestart.Instantly) {
            missions.remove(missionName);
            missionLevels.remove(missionName);
        }
    }

    public void calculateIslandValue() {
        if (!getCenter().getWorld().isChunkLoaded(getCenter().getBlockX() >> 4, getCenter().getBlockZ() >> 4)) return;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();

        final BlockValues blockValues = IridiumSkyblock.getBlockValues();
        final Map<XMaterial, Double> blockValueMap = blockValues.blockvalue;

        BigDecimal value = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : valuableBlocks.entrySet()) {
            final String item = entry.getKey();
            final BigDecimal amount = BigDecimal.valueOf(entry.getValue());
            if (amount.intValue() < 1) continue;
            final Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(item);
            if (!xmaterial.isPresent()) continue;

            if (!blockValueMap.containsKey(xmaterial.get())) continue;
            final BigDecimal blockValue = BigDecimal.valueOf(blockValueMap.get(xmaterial.get()));

            value = value.add(blockValue.multiply(amount));
        }

        final Config config = IridiumSkyblock.getConfiguration();

        final Set<World> worlds = new HashSet<>();
        final World islandWorld = IslandManager.getWorld();
        worlds.add(islandWorld);

        if (config.netherIslands) {
            final World netherIslandWorld = IslandManager.getNetherWorld();
            worlds.add(netherIslandWorld);
        }

        final Chunk pos1Chunk = pos1.getChunk();
        final int minChunkX = pos1Chunk.getX();
        final int minChunkZ = pos1Chunk.getZ();

        final Chunk pos2Chunk = pos2.getChunk();
        final int maxChunkX = pos2Chunk.getX();
        final int maxChunkZ = pos2Chunk.getZ();

        final double minX = pos1.getX();
        final double minZ = pos1.getZ();
        final double maxX = pos2.getX();
        final double maxZ = pos2.getZ();

        final Map<String, Double> spawnerValueMap = blockValues.spawnervalue;

        Function<CreatureSpawner, Integer> getSpawnerAmount;
        SpawnerSupport spawnerSupport = IridiumSkyblock.getInstance().getSpawnerSupport();
        if (spawnerSupport != null) {
            getSpawnerAmount = spawnerSupport::getSpawnerAmount;
        } else {
            getSpawnerAmount = null;
        }

        spawners.clear();

        for (World world : worlds) {
            for (int X = minChunkX; X <= maxChunkX; X++) {
                for (int Z = minChunkZ; Z <= maxChunkZ; Z++) {
                    final Chunk chunk = world.getChunkAt(X, Z);
                    for (BlockState state : chunk.getTileEntities()) {
                        if (!isInIsland(state.getLocation())) continue;
                        if (!(state instanceof CreatureSpawner)) continue;

                        final CreatureSpawner spawner = (CreatureSpawner) state;
                        final Location location = spawner.getLocation();
                        final double x = location.getX();
                        final double z = location.getZ();
                        if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                        final EntityType type = spawner.getSpawnedType();
                        final String typeName = type.name();
                        if (!spawnerValueMap.containsKey(typeName)) continue;
                        final BigDecimal spawnerValue = BigDecimal.valueOf(spawnerValueMap.get(typeName));

                        final BigDecimal amount = (getSpawnerAmount == null) ? BigDecimal.ONE : BigDecimal.valueOf(getSpawnerAmount.apply(spawner));
                        spawners.compute(typeName, (name, original) -> {
                            if (original == null) return amount.intValue();
                            return original + amount.intValue();
                        });

                        value = value.add(spawnerValue.multiply(amount));
                    }
                }
            }
        }

        this.value = value.doubleValue();
        if (startvalue == -1) startvalue = value.doubleValue();

        for (Mission mission : IridiumSkyblock.getMissions().missions) {
            missionLevels.putIfAbsent(mission.name, 1);
            if (mission.levels.get(missionLevels.get(mission.name)).type == MissionType.VALUE_INCREASE) {
                setMission(mission.name, (int) (value.doubleValue() - startvalue - lastMissionValue));
            }
        }
        this.value += this.extravalue;
        if (IridiumSkyblock.getConfiguration().islandMoneyPerValue != 0)
            this.value += this.money / IridiumSkyblock.getConfiguration().islandMoneyPerValue;

        IslandWorthCalculatedEvent islandWorthCalculatedEvent = new IslandWorthCalculatedEvent(this, this.value);
        Bukkit.getPluginManager().callEvent(islandWorthCalculatedEvent);
        this.value = islandWorthCalculatedEvent.getIslandWorth();
        IslandDataManager.save(this, true);
    }

    public void addWarp(Player player, Location location, String name, String password) {
        if (warps.size() < IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(warpLevel).size) {
            warps.add(new Warp(location, name, password));
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().warpAdded.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().maxWarpsReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    public void addUser(User user) {
        if (members.size() < IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(memberLevel).size) {

            for (String player : members) {
                User u = User.getUser(player);
                Player p = Bukkit.getPlayer(u.name);
                if (p != null) {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerJoinedYourIsland.replace("%player%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            bans.remove(user.player);
            user.islandID = id;
            user.role = Role.Member;
            user.invites.clear();
            members.add(user.player);
            teleportHome(Bukkit.getPlayer(user.name));
        } else {
            Player player = Bukkit.getPlayer(user.name);
            if (player != null) {
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().maxMemberCount.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }

    public void removeUser(User user) {
        user.islandID = 0;
        Player player = Bukkit.getPlayer(user.name);
        if (player != null) {
            spawnPlayer(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        members.remove(user.player);
        user.role = Role.Visitor;
        for (String member : members) {
            User u = User.getUser(member);
            Player p = Bukkit.getPlayer(u.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().kickedMember.replace("%member%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        getMembersGUI().getInventory().clear();
        getMembersGUI().addContent();
    }

    public boolean isInIsland(Location location) {
        if (location == null) return false;
        return isInIsland(location.getX(), location.getZ());
    }

    public boolean isInIsland(double x, double z) {
        return x >= pos1.getX()
                && x <= pos2.getX()
                && z >= pos1.getZ()
                && z <= pos2.getZ();
    }

    public void init() {
        if (getNetherschematic() == null) {
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematics) {
                if (fakeSchematic.name.equals(getSchematic())) {
                    setNetherschematic(fakeSchematic.netherisland);
                }
            }
        }
        if (biome == null) biome = IridiumSkyblock.getConfiguration().defaultBiome;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();
        if (stackedBlocks == null) stackedBlocks = new ConcurrentHashMap<>();
        if (members == null) {
            members = new HashSet<>();
            members.add(owner);
        }
        this.playersOnIsland = new HashSet<>();
        this.lastPlayerCaching = 0L;
        upgradeGUI = new UpgradeGUI(this);
        boosterGUI = new BoosterGUI(this);
        missionsGUI = new MissionsGUI(this);
        membersGUI = new MembersGUI(this);
        warpGUI = new WarpGUI(this);
        borderColorGUI = new BorderColorGUI(this);
        schematicSelectGUI = new SchematicSelectGUI(this);
        permissionsGUI = new PermissionsGUI(this);
        islandMenuGUI = new IslandMenuGUI(this);
        islandAdminGUI = new IslandAdminGUI(this);
        coopGUI = new CoopGUI(this);
        bankGUI = new BankGUI(this);
        biomeGUI = new BiomeGUI(this, World.Environment.NORMAL);
        netherBiomeGUI = new BiomeGUI(this, World.Environment.NETHER);
        visitorGUI = new VisitorGUI(this);

        failedGenerators = new HashSet<>();
        coopInvites = new HashSet<>();
        boosterid = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), () -> {
            if (spawnerBooster > 0) spawnerBooster--;
            if (farmingBooster > 0) farmingBooster--;
            if (expBooster > 0) expBooster--;
            if (flightBooster == 1) {
                for (String player : members) {
                    Player p = Bukkit.getPlayer(player);
                    if (p != null) {
                        if ((!p.hasPermission("IridiumSkyblock.Fly") && !p.hasPermission("iridiumskyblock.fly")) && p.getGameMode().equals(GameMode.SURVIVAL)) {
                            p.setAllowFlight(false);
                            p.setFlying(false);
                            User.getUser(p).flying = false;
                        }
                    }
                }
            }
            if (flightBooster > 0) flightBooster--;
        }, 0, 20);
        if (permissions == null) {
            permissions = new HashMap<Role, Permissions>() {{
                for (Role role : Role.values()) {
                    put(role, new Permissions());
                }
            }};
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), (Runnable) this::sendBorder, 20);
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), (Runnable) this::sendHomograms, 20);
    }

    public long canGenerate() {
        if (lastRegen == null) return 0;
        if (new Date().after(lastRegen)) return 0;
        return lastRegen.getTime() - System.currentTimeMillis();
    }

    public void teleportPlayersHome() {
        for (Player p : getPlayersOnIsland()) {
            teleportHome(p);
        }
    }

    public void pasteSchematic(boolean deleteBlocks) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, IridiumSkyblock.getConfiguration().regenCooldown);
        lastRegen = c.getTime();
        if (deleteBlocks) deleteBlocks();
        pasteSchematic();
        killEntities();
        //Reset island home
        for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematics) {
            if (!schematic.name.equals(this.schematic)) continue;
            home = new Location(IslandManager.getWorld(), getCenter().getX() + schematic.x, schematic.y, getCenter().getZ() + schematic.z);
            this.setBiome(schematic.biome);
        }
    }

    public void pasteSchematic(Player player, boolean deleteBlocks) {
        pasteSchematic(deleteBlocks);
        User.getUser(player).teleportingHome = false;
        teleportHome(player);
        sendBorder(player);
        IridiumSkyblock.nms.sendTitle(player, IridiumSkyblock.getMessages().islandCreated, 20, 40, 20);
        if (!IridiumSkyblock.getMessages().islandCreatedSubtitle.isEmpty())
            IridiumSkyblock.nms.sendSubTitle(player, IridiumSkyblock.getMessages().islandCreatedSubtitle, 20, 40, 20);
    }

    private void pasteSchematic() {
        stackedBlocks.clear();
        for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematics) {
            if (!fakeSchematic.name.equals(this.schematic)) continue;
            IridiumSkyblock.worldEdit.paste(new File(IridiumSkyblock.schematicFolder, schematic), getCenter().clone().add(fakeSchematic.xOffset, fakeSchematic.yOffset, fakeSchematic.zOffset), this);
            Location center = getCenter().clone();
            if (IridiumSkyblock.getConfiguration().netherIslands) {
                center.setWorld(IslandManager.getNetherWorld());
                IridiumSkyblock.worldEdit.paste(new File(IridiumSkyblock.schematicFolder, netherschematic), center.clone().add(fakeSchematic.xNetherOffset, fakeSchematic.yNetherOffset, fakeSchematic.zNetherOffset), this);
            }
        }
    }

    public void clearInventories() {
        if (IridiumSkyblock.getConfiguration().clearInventories) {
            for (String player : members) {
                User user = User.getUser(player);
                Player p = Bukkit.getPlayer(user.name);
                if (p != null) {
                    p.getInventory().clear();
                    if (IridiumSkyblock.getConfiguration().clearEnderChests) p.getEnderChest().clear();
                }
            }
        }
    }

    public void sendHomograms(Player player) {
        World world = player.getWorld();
        User user = User.getUser(player);
        for (Object object : user.getHolograms()) {
            IridiumSkyblock.nms.removeHologram(player, object);
        }
        user.clearHolograms();
        for (Location location : stackedBlocks.keySet()) {
            if (location.getWorld() != world) continue;
            Block block = location.getBlock();
            int amount = stackedBlocks.get(location);
            IridiumSkyblock.nms.sendHologram(player, block.getLocation().add(0.5, -0.5, 0.5), Utils.processMultiplePlaceholders(IridiumSkyblock.getMessages().stackedBlocksHologram, Arrays.asList(new Utils.Placeholder("amount", amount + ""), new Utils.Placeholder("block", XMaterial.matchXMaterial(block.getType()).toString()))));
        }
    }

    public void sendHomograms() {
        for (Player player : getPlayersOnIsland()) {
            sendHomograms(player);
        }
    }

    public void teleportHome(Player p) {
        if (getHome() == null) home = getCenter();
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString()) && !p.hasPermission("iridiumskyblock.visitbypass")) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getSchematics().schematics.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematics) {
                        setSchematic(schematic.name);
                        setNetherschematic(schematic.netherisland);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
        if (Utils.isSafe(getHome(), this)) {
            p.teleport(getHome());
            sendBorder(p);
        } else {
            Location loc = Utils.getNewHome(this, this.home);
            if (loc != null) {
                this.home = loc;
                p.teleport(this.home);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, true);
            }
        }
    }

    public void teleportNetherHome(Player p) {
        Location netherhome = getCenter().clone();
        netherhome.setWorld(IslandManager.getNetherWorld());
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (getSchematic() == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getSchematics().schematics.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematics) {
                        setSchematic(schematic.name);
                    }
                } else {
                    p.openInventory(getSchematicSelectGUI().getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            for (String pl : members) {
                Player player = Bukkit.getPlayer(User.getUser(pl).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", p.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
        if (Utils.isSafe(getNetherhome(), this)) {
            p.teleport(getNetherhome());
            sendBorder(p);
        } else {

            Location loc = Utils.getNewHome(this, netherhome);
            if (loc != null) {
                p.teleport(netherhome);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, false);
            }
        }
    }

    public void delete() {
        Bukkit.getPluginManager().callEvent(new IslandDeleteEvent(this));

        Bukkit.getScheduler().cancelTask(getMembersGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getBoosterGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getMissionsGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getUpgradeGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getWarpGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getPermissionsGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getIslandMenuGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getIslandAdminGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getCoopGUI().scheduler);
        Bukkit.getScheduler().cancelTask(getBankGUI().scheduler);
        if (generateID != -1) Bukkit.getScheduler().cancelTask(generateID);
        permissions.clear();
        spawnPlayers();
        for (String player : members) {
            User.getUser(player).islandID = 0;
            Player p = Bukkit.getPlayer(User.getUser(player).name);
            if (p != null) {
                p.closeInventory();
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandDeleted.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        killEntities();
        deleteBlocks();
        for (int id : coop) {
            IslandManager.getIslandViaId(id).coop.remove(getId());
        }
        coop = null;
        hideBorder();
        this.owner = null;
        this.pos1 = null;
        this.pos2 = null;
        this.members = null;
        this.center = null;
        this.home = null;
        IslandManager.removeIsland(this);
        this.id = 0;
        IridiumSkyblock.getInstance().saveConfigs();
        Bukkit.getScheduler().cancelTask(boosterid);
        boosterid = -1;
    }

    public void removeBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.remove(user.player);
    }

    public void addBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.add(user.player);
    }

    public void removeVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.remove(user.player);
        IslandDataManager.save(this, true);
    }

    public void addVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.add(user.player);
        IslandDataManager.save(this, true);
    }

    public boolean hasVoted(User user) {
        if (votes == null) votes = new HashSet<>();
        return votes.contains(user.player);
    }

    public int getVotes() {
        if (votes == null) votes = new HashSet<>();
        return votes.size();
    }

    public boolean isBanned(User user) {
        if (bans == null) bans = new HashSet<>();
        return bans.contains(user.player) && !user.bypassing;
    }

    public void addCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        for (String member : island.getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopGiven.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopAdded.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        coop.add(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.add(id);
    }

    public void inviteCoop(Island island) {
        if (coopInvites == null) coopInvites = new HashSet<>();
        coopInvites.add(island.getId());
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(IridiumSkyblock.getMessages().coopInvite.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is coop " + User.getUser(island.getOwner()).name);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumSkyblock.getMessages().coopHoverMessage).create());
                for (BaseComponent component : components) {
                    component.setClickEvent(clickEvent);
                    component.setHoverEvent(hoverEvent);
                }
                pl.getPlayer().spigot().sendMessage(components);
            }
        }
    }

    public void removeCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.remove(id);
        for (String member : island.getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : getMembers()) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(island.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        getCoopGUI().getInventory().clear();
        getCoopGUI().addContent();
        island.getCoopGUI().getInventory().clear();
        island.getCoopGUI().addContent();
    }

    public void removeCoop(int id) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(id);
    }

    public boolean isCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        if (island == null) return false;
        return coop.contains(island.id);
    }

    public Set<Integer> getCoop() {
        if (coop == null) coop = new HashSet<>();
        return coop;
    }

    public void spawnPlayers() {
        for (Player p : getPlayersOnIsland()) {
            spawnPlayer(p);
        }
    }

    public Set<Player> getPlayersOnIsland() {
        if (System.currentTimeMillis() >= lastPlayerCaching + (IridiumSkyblock.getConfiguration().playersOnIslandRefreshTime * 1000L)) {
            reloadPlayersOnIsland();
        }
        return playersOnIsland;
    }

    public void reloadPlayersOnIsland() {
        lastPlayerCaching = System.currentTimeMillis();
        playersOnIsland.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInIsland(p.getLocation())) {
                playersOnIsland.add(p);
            }
        }
    }

    public void spawnPlayer(Player player) {
        if (player == null) return;
        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
            EssentialsSpawn essentialsSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
        } else {
            World world = Bukkit.getWorld(IridiumSkyblock.getConfiguration().worldSpawn);
            if (world == null) world = Bukkit.getWorlds().get(0);
            player.teleport(world.getSpawnLocation());
        }
        playersOnIsland.remove(player);
    }

    public void setBiome(XBiome biome) {
        this.biome = biome;
        final World world = IslandManager.getWorld();
        final List<Chunk> chunks = new ArrayList<Chunk>() {{
            for (int X = getPos1().getChunk().getX(); X <= getPos2().getChunk().getX(); X++) {
                for (int Z = getPos1().getChunk().getZ(); Z <= getPos2().getChunk().getZ(); Z++) {
                    add(world.getChunkAt(X, Z));
                }
            }
        }};
        biome.setBiome(getPos1(), getPos2()).thenRunAsync(() -> {
            for (Chunk c : chunks) {
                for (Player p : world.getPlayers()) {
                    if (p.getLocation().getWorld() == world) {
                        IridiumSkyblock.nms.sendChunk(p, c);
                    }
                }
            }
        });
    }

    public void setNetherBiome(XBiome biome) {
        if (!IridiumSkyblock.getConfiguration().netherIslands) return;
        this.netherBiome = biome;
        final World world = IslandManager.getNetherWorld();
        Location pos1 = getPos1();
        Location pos2 = getPos2();
        pos1.setWorld(world);
        pos2.setWorld(world);
        biome.setBiome(pos1, pos2).thenRunAsync(() -> {
            for (int X = getPos1().getChunk().getX(); X <= getPos2().getChunk().getX(); X++) {
                for (int Z = getPos1().getChunk().getZ(); Z <= getPos2().getChunk().getZ(); Z++) {
                    for (Player p : world.getPlayers()) {
                        if (p.getLocation().getWorld() == world) {
                            IridiumSkyblock.nms.sendChunk(p, world.getChunkAt(X, Z));
                        }
                    }
                }
            }
        });
    }

    public void deleteBlocks() {
        clearInventories();
        valuableBlocks.clear();
        calculateIslandValue();
        final World world = IslandManager.getWorld();
        final World nether = IslandManager.getNetherWorld();
        for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
            for (int Y = 0; Y <= 255; Y++) {
                for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                    IridiumSkyblock.nms.setBlockFast(world.getBlockAt(X, Y, Z), 0, (byte) 0);
                }
            }
        }
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            for (int X = getPos1().getBlockX(); X <= getPos2().getBlockX(); X++) {
                for (int Y = 0; Y <= 255; Y++) {
                    for (int Z = getPos1().getBlockZ(); Z <= getPos2().getBlockZ(); Z++) {
                        IridiumSkyblock.nms.setBlockFast(nether.getBlockAt(X, Y, Z), 0, (byte) 0);
                    }
                }
            }
        }
    }

    public void killEntities() {
        for (Entity entity : IslandManager.getWorld().getNearbyEntities(getCenter(), IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 255, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00)) {
            if (!entity.getType().equals(EntityType.PLAYER)) {
                entity.remove();
            }
        }
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            Location netherCenter = getCenter().clone();
            netherCenter.setWorld(IslandManager.getNetherWorld());

            for (Entity entity : IslandManager.getNetherWorld().getNearbyEntities(netherCenter, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 255, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00)) {
                if (!entity.getType().equals(EntityType.PLAYER)) {
                    entity.remove();
                }
            }
        }
    }

    public void addExtraValue(double amount) {
        this.extravalue += amount;
    }

    public void removeExtraValue(double amount) {
        this.extravalue -= amount;
    }


    public Location getNetherhome() {
        Location netherhome = getCenter().clone();
        netherhome.setWorld(IslandManager.getNetherWorld());
        return netherhome;
    }

    public void setOwner(OfflinePlayer owner) {
        for (String player : members) {
            User user = User.getUser(player);
            Player p = Bukkit.getPlayer(user.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().transferdOwnership.replace("%player%", owner.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

            }
        }
        User.getUser(getOwner()).role = Role.CoOwner;
        this.owner = owner.getUniqueId().toString();
        User.getUser(getOwner()).role = Role.Owner;
    }

    public void setSizeLevel(int sizeLevel) {
        this.sizeLevel = sizeLevel;

        pos1 = getCenter().clone().subtract(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        pos2 = getCenter().clone().add(IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00, 0, IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(sizeLevel).size / 2.00);
        sendBorder();
        setBiome(biome);
    }

    public void removeWarp(Warp warp) {
        warps.remove(warp);
    }

    public String getName() {
        if (name == null) name = User.getUser(getOwner()).name;
        return name;
    }

    public Map<String, Integer> getMissionLevels() {
        if (missionLevels == null) missionLevels = new HashMap<>();
        return missionLevels;
    }

    public String getFormattedValue() {
        return Utils.NumberFormatter.format(value);
    }

    public String getFormattedMoney() {
        return Utils.NumberFormatter.format(money);
    }

    public String getFormattedExp() {
        return Utils.NumberFormatter.format(exp);
    }

    public String getFormattedCrystals() {
        return Utils.NumberFormatter.format(crystals);
    }

    public void save(boolean async) {
        IslandManager.save(this, async);
    }
}
