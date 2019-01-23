package it.polito.tdp.flightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Model {
	
	private FlightDelaysDAO dao;
	private List<Airline> airlines;
	Graph< Airport ,DefaultWeightedEdge> graph = null; 
	private List<AirportsAndWeight> resultTot;
	List<Airport> airport;
	
	
	public Model() {
		this.dao = new FlightDelaysDAO();
		this.airlines = this.dao.loadAllAirlines();
		airport = this.dao.loadAllAirports();
		resultTot = new ArrayList<>();
		
		
	}
	
	public List<Airline> getAllAirlines(){
		return airlines;
	}
	
	public Airport getAirportOrigin(Flight flight) {
		for(Airport a : airport) {
			if(a.getId().equals(flight.getOriginAirportId())) {
				return a;
			}
		}
		return null;
	}
	public Airport getAirportDestination(Flight flight) {
		for(Airport a : airport) {
			if(flight.getDestinationAirportId().equals(a.getId())) {
				return a;
			}
		}
		return null;
	}
	
	public void creaGrafo(Airline airline) {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class); 
		double avg;
		double latLang;
		double result;
		Graphs.addAllVertices(graph, airport);
		for(Flight f : this.dao.loadAllFlightAir(airline)) {
			avg = 0.0;
			latLang = 0.0;
			result = 0.0;
				Airport a1 = this.getAirportOrigin(f);
				Airport a2 = this.getAirportDestination(f);
							if(a1!=null && a2!=null) {
								avg = this.dao.getAVG(a1, a2);
								LatLng l1 = new LatLng(a1.getLatitude(),a1.getLongitude());
								LatLng l2 = new LatLng(a2.getLatitude(),a2.getLongitude());
								latLang = LatLngTool.distance(l1, l2, LengthUnit.KILOMETER);
								result = avg/latLang;
								//resultTot.add(new AirportsAndWeight(f,result));
								Graphs.addEdge(graph, a1, a2, result);
							}
		}
		for(DefaultWeightedEdge aw : this.graph.edgeSet()) {
			
			resultTot.add(new AirportsAndWeight(this.graph.getEdgeSource(aw),this.graph.getEdgeTarget(aw),this.graph.getEdgeWeight(aw)));
		}
		System.out.println("Grafo creato! (airline: " + airline + ")");
		System.out.println("# Vertici: " + graph.vertexSet().size());
		for(DefaultWeightedEdge f : graph.edgeSet()) {
		System.out.println("# Archi: " + f);}
		System.out.println("# Archi: " + graph.edgeSet().size());
		
	}
	
	public List<AirportsAndWeight> getResult(){
			Collections.sort(resultTot);
			return resultTot;
		}
	

}
