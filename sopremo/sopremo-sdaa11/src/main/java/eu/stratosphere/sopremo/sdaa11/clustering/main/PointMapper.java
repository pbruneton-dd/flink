/***********************************************************************************************************************
 *
 * Copyright (C) 2010 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package eu.stratosphere.sopremo.sdaa11.clustering.main;

import eu.stratosphere.sopremo.ElementaryOperator;
import eu.stratosphere.sopremo.InputCardinality;
import eu.stratosphere.sopremo.OutputCardinality;
import eu.stratosphere.sopremo.pact.JsonCollector;
import eu.stratosphere.sopremo.pact.SopremoCross;
import eu.stratosphere.sopremo.sdaa11.clustering.Point;
import eu.stratosphere.sopremo.sdaa11.clustering.json.PointNodes;
import eu.stratosphere.sopremo.sdaa11.clustering.tree.ClusterTree;
import eu.stratosphere.sopremo.type.IJsonNode;
import eu.stratosphere.sopremo.type.ObjectNode;
import eu.stratosphere.sopremo.type.TextNode;

/**
 * Inputs:
 * <ol>
 * <li>Points</li>
 * <li>Tree</li>
 * </ol>
 * Outputs:
 * <ol>
 * <li>Assigned points</li>
 * </ol>
 * 
 * @author skruse
 * 
 */
@InputCardinality(value = 2)
@OutputCardinality(value = 1)
public class PointMapper extends ElementaryOperator<PointMapper> {

	private static final long serialVersionUID = -1539853388756701551L;

	public static final int TREE_INPUT_INDEX = 0;
	public static final int POINT_INPUT_INDEX = 1;

	public static class Implementation extends SopremoCross {

		private final TextNode clusterIdNode = new TextNode();

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * eu.stratosphere.sopremo.pact.SopremoCross#cross(eu.stratosphere.sopremo
		 * .type.IJsonNode, eu.stratosphere.sopremo.type.IJsonNode,
		 * eu.stratosphere.sopremo.pact.JsonCollector)
		 */
		@Override
		protected void cross(final IJsonNode pointNode,
				final IJsonNode treeNode, final JsonCollector out) {

			// System.out.println("Cross tree " + treeNode);
			// System.out.println("x point " + pointNode);
			final ClusterTree tree = new ClusterTree();
			tree.read(treeNode);

			final Point point = new Point();
			point.read(pointNode);

			final String clusterId = tree.findIdOfClusterNextTo(point);
			this.clusterIdNode.setValue(clusterId);

			PointNodes
					.assignCluster((ObjectNode) pointNode, this.clusterIdNode);

			out.collect(pointNode);
		}

	}

}
