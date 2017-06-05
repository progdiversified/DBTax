package org.dbpedia.dbtax.DBTaxProject;

import org.dbpedia.dbtax.DBTaxProject.database.LeafExtractionDB;

public class DBTaxPipeline {

	public static void main(String[] args){
		System.out.println("DBTax:: hello world");
		//Stage 1: Extract Leaf Nodes
		LeafExtractionDB.extractLeaves();
		System.out.println("DBTax:: Stage 1 is completed.");
		
		//Stage 2: Find Prominent Nodes
		NodeUtils.findProminentNodes();
		
		System.out.println("End of World !!");
	}
	
}
