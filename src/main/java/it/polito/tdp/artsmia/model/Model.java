package it.polito.tdp.artsmia.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.artsmia.db.ArtsmiaDAO;

public class Model {

	private Graph<ArtObject, DefaultWeightedEdge> grafo;
	private ArtsmiaDAO dao;
	private Map<Integer, ArtObject> idMap;
	
	public Model() {
		dao = new ArtsmiaDAO();
		idMap = new HashMap<>();
	}
	
	//Creo il grafo qua dentro per essere sicuro che, nel caso l'utente voglia rifarlo, il precedente venga cancellato 
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class); 
		
		//Aggiungere i vertici (in questo caso li dobbiamo aggiungere tutti senza filtri)
		//1. -> Recupero tutti gli ArtObject dal db
		//2. -> Li inserisco come vertici
		dao.listObjects(idMap);
		Graphs.addAllVertices(grafo, idMap.values());
		
		//Aggiungere gli archi
		// APPROCCIO 1 ----> raramente porta ad una soluzione in tempi ragionevoli (solo in caso di max 50 vertici)!!!!
		// (Possiamo più o meno calcolare il tempo preventivamente guardando quanto tempo ci impiega la singola query su HeidiSQL 
		// e moltiplicandolo per il numero di cicli che deve eseguire un singolo for al quadrato)
		// [in questo caso ci metterebbe circa 620 gg]
		// -> doppo ciclo for sui vertici 
		// -> dati due vertici, controllo se sono collegati
		/*
		for(ArtObject a1 : this.grafo.vertexSet()) {
			for(ArtObject a2 : this.grafo.vertexSet()) {
				if(!a1.equals(a2) && !this.grafo.containsEdge(a1, a2)) {
					//devo collegare a1 ad a2?
					int peso = dao.getPeso(a1, a2);
					if(peso>0) {
						Graphs.addEdge(this.grafo, a1, a2, peso);
					}
				}
			}		
		}*/
		
		// APPROCCIO 3
		
		for(Adiacenza a : dao.getAdiacenze()) 
			Graphs.addEdge(this.grafo, idMap.get(a.getId1()), idMap.get(a.getId2()), a.getPeso());
		
		System.out.println("GRAFO CREATO");
		System.out.println("# VERTICI: " + grafo.vertexSet().size());
		System.out.println("# ARCHI: " + grafo.edgeSet().size());


	}
}
