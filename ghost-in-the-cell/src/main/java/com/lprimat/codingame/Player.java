package com.lprimat.codingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	
    private static final String FACTORY = "FACTORY";
    private static Map<Integer, Factory> factories = new HashMap<>();
    private static List<Factory> myfactories = new ArrayList<>();
    private static List<Troop> troops = new ArrayList<>();
    
    //seed=979788818
    //turn 28
	public static void main(String args[]) {
        //Scanner in = new Scanner(System.in);
        Scanner in = new Scanner("9 36 0 1 4  0 2 4 0 3 2 0 4 2 0 5 5 0 6 5 0 7 6 0 8 6 1 2 9 1 3 1 1 4 7 1 5 2 1 6 10 1 7 1 1 8 12 2 3 7 2 4 1 2 5 10 2 6 2 2 7 12 2 8 1 3 4 6 3 5 1 3 6 8 3 7 4 3 8 10 4 5 8 4 6 1 4 7 10 4 8 4 5 6 11 5 7 3 5 8 12 6 7 12 6 8 3 7 8 14 11 0 FACTORY 0 0 0 0 0 1 FACTORY 1 28 2 0 0 2 FACTORY -1 54 2 0 0 3 FACTORY 1 37 3 0 0 4 FACTORY 0 13 3 0 0 5 FACTORY 1 24 3 0 0 6 FACTORY 0 3 3 0 0 7 FACTORY 1 5 1 0 0 8 FACTORY -1 8 1 0 0 16 TROOP -1 8 7 2 7 22 TROOP -1 8 6 2 1");
        int factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories
        
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            
            createOrEnrichFactories(factories, factory1, factory2, distance);
            createOrEnrichFactories(factories, factory2, factory1, distance);
        }

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();
                
                System.err.println(entityId + " " + entityType + " " + arg1 + " " + arg2 + " " + arg3 + " " + arg4 + " " + arg5);
                if (entityType.equals(FACTORY)) {
                	Factory f = factories.get(entityId);
                	enrichFactory(f, arg1, arg2, arg3, arg4, arg5);
                } else {
                	createTroops(entityId, arg1, arg2, arg3, arg4, arg5);
                }
            }
            
            createMyFactories();
            Target target = chooseTarget();
            if (target != null) {
            	System.out.println("MOVE" + " " + target.source.id + " " + target.dest.id + " " + target.cyborgCount);
            } else {
            	System.out.println("WAIT");
            }
        }
    }

	private static void createMyFactories() {
		Iterator<Map.Entry<Integer, Factory>> iter = factories.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Factory> entry = iter.next();
			Factory f = entry.getValue();
			if (f.ownerId == 1) {
				myfactories.add(f);
				//iter.remove();
			}
		}
	}

	private static Target chooseTarget() {
		Target target = null;
		int minDist = Integer.MAX_VALUE;
		for (Factory myF : myfactories) {
			//pour toutes les factory qu'on posséde et qui ont des cyborg on trouve la
			//cible avec la meilleur prod et qu'on puisse capturer le plus rapidement
			Target localTarget = getTargetWithBestProductionAndClosest(myF);
			if (localTarget.dist < minDist) {
				target = localTarget;
				minDist = localTarget.dist;
			}
		}
		
		return target;
	}

	private static Target getTargetWithBestProductionAndClosest(Factory source) {
		Target target = null;
		int maxProd = 0;
		int minDist = Integer.MAX_VALUE;
		
		Collection<Factory> factList = factories.values(); 
		for (Factory fact : factList) {
			int distFromSource = getDistFromTo(source, fact);
			if (fact.ownerId != 1 && fact.production > maxProd && distFromSource < minDist) {
				target = new Target(source, fact, fact.nbCyborg, distFromSource);
				maxProd = fact.production;
				minDist = distFromSource;
			}
		}
		
		return target;
	}

	private static int getDistFromTo(Factory source, Factory fact) {
		Link link = source.links.get(fact.id); 
		if (link != null) {
			return link.distance; 
		}
		return Integer.MAX_VALUE;
	}

	private static void createOrEnrichFactories(Map<Integer, Factory> factories, int source, int target, int distance) {
		if (factories.containsKey(source)) {
			factories.get(source).links.put(target, new Link(target, distance));
		} else {
			Factory f = new Factory(source, target, distance);
			factories.put(source, f);
		}
	}
	
	private static void enrichFactory(Factory f, int ownerId, int nbCyborg, int production, int arg4, int arg5) {
		f.ownerId = ownerId;
		f.nbCyborg = nbCyborg;
		f.production = production;
	}

	private static void createTroops(int id, int ownerId, int source, int target, int nbCyborg, int remainingTurn) {
		Troop t = new Troop(id, ownerId, source, target, nbCyborg, remainingTurn);
		troops.add(t);
	}
}

class Factory {
	public int id;
	public int ownerId;
	public int nbCyborg;
	public int production;
	public Map<Integer, Link> links;
	
	public Factory(int id, int dest, int distance) {
		this.id = id;
		this.links = new HashMap<>();
		links.put(dest, new Link(dest, distance));
	}
	
}

class Link {
	public int target;
	public int distance;
	
	public Link(int target, int distance) {
		this.target = target;
		this.distance = distance;
	}
}

class Troop {
	public int id;
	public int ownerId;
	public int source;
	public int target;
	public int nbCyborg;
	public int remainingTurn;
	
	public Troop(int id, int ownerId, int source, int target, int nbCyborg, int remainingTurn) {
		this.id = id;
		this.ownerId = ownerId;
		this.source = source;
		this.target = target;
		this.nbCyborg = nbCyborg;
		this.remainingTurn = remainingTurn;
	}
}

class Target {
	public Factory source;
	public Factory dest;
	public int cyborgCount;
	public int dist;
	
	public Target(Factory source, Factory dest, int cyborgCount, int dist) {
		super();
		this.source = source;
		this.dest = dest;
		this.cyborgCount = cyborgCount;
		this.dist = dist;
	}
}