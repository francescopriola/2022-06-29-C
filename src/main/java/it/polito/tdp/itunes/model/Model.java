package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private Graph<Album, DefaultWeightedEdge> graph;
	private ItunesDAO dao;
	
	public Model() {
		this.dao = new ItunesDAO(); 
	}
	
	public void creaGrafo(int n) {
		this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.graph, this.dao.getVertex(n));
		
		for(Album a1: this.graph.vertexSet()) {
			for(Album a2: this.graph.vertexSet()) {
				if(!a1.equals(a2)) {
					DefaultWeightedEdge edge = this.graph.getEdge(a1, a2);
					DefaultWeightedEdge edgeBack = this.graph.getEdge(a2, a1);
				
					double weight = Math.abs((int)a1.getPrezzo()-(int)a2.getPrezzo());
				
					if(edge == null && edgeBack == null && a1.getPrezzo() != a2.getPrezzo()) 
						edge = Graphs.addEdgeWithVertices(this.graph, a1, a2, weight);
				}
			}
		}
		
		for(Album a: this.graph.vertexSet()) {
			double sommaIn = 0;
			for(DefaultWeightedEdge entrante: this.graph.incomingEdgesOf(a)) {
				sommaIn += (int)this.graph.getEdgeWeight(entrante);
			}
			
//			double sommaOut = 0;
//			for(DefaultWeightedEdge uscente: this.graph.outgoingEdgesOf(a)) {
//				sommaOut += (int)this.graph.getEdgeWeight(uscente);
//			}
			
			double bilancio = Math.round((sommaIn/this.graph.inDegreeOf(a))*100.0)/100.0;
			a.setBilancio(bilancio);
		}
		
		System.out.println("#VERTICI: " + this.graph.vertexSet().size());
		System.out.println("#ARCHI: " + this.graph.edgeSet().size());
	}
	
	public String getNVertex() {
		return "#VERTICI: " + this.graph.vertexSet().size();
	}
	
	public String getNEdge() {
		return "#ARCHI: " + this.graph.edgeSet().size();
	}
	
	public Graph<Album, DefaultWeightedEdge> getGraph(){
		return this.graph;
	}
	
	public String getAdiacenze(Album a1){
		String res = "";
		List<Album> result = new ArrayList<>();
		
		for(Album a : Graphs.neighborListOf(this.graph, a1)) {
			result.add(a);
		}
		
		Collections.sort(result);
		
		for(Album a : result) {
			res += a + ", bilancio= " + a.getBilancio() + "\n";
		}
		
		return res;
		
	}
	
}
