package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.dbpedia.dbtax.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * Extract Leaves Function adds the leaf nodes and their corresponding Edges 
 */

public class LeafExtractionDB {


    private static final Logger logger = LoggerFactory.getLogger(LeafExtractionDB.class);

	private LeafExtractionDB(){ }
	public static void extractLeaves(){

		int threshold = 56; //ThresholdCalculations.findThreshold();

		String query = "SELECT cat_title "
				+ "FROM category "
				+ "WHERE cat_title in "
				+ "(select cl_to from "
				+ "categorylinks "
				+ "WHERE cl_from in "
				+ "(select page_id "
				+ "from page "
				+ "where page_namespace =0)) "
				+ "AND cat_subcats=0 "
				+ "AND cat_pages>0 "
				+ "AND cat_pages< "+threshold;
		/*
		 * Threshold Value is calculated.
		 * The above query gets all categories names of the actual article pages 
		 * With no sub categories
		 */
		Set<Node> nodeMap = new HashSet<>();

		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			
			ResultSet rs = ps.executeQuery();
			logger.info("Excuted the query to retrieve the leaves");

			//We loop through the entire result set of leaves.
			while ( rs.next() ){

				String catName = rs.getString("cat_title").trim();

				//Get the page id of category 
				int pageid = PageDB.getPageId(catName);

				if(pageid!=-1){
					//Add the leaf node to the database Node table
					nodeMap.add(new Node(pageid,catName));

                    //The below function is to find the immediate parents of the categories
					//And add them to corresponding node and edge databases
					CategoryLinksDB.getCategoryParentsByPageID(pageid);
				}
			}
		} catch ( SQLException e ){
			logger.error(e.getMessage());
		}

        NodeDB.insertNode(nodeMap);
	}
}