package org.recap.graph;

public class Edge {
    private final String targetNode;  // 연결되는 노드
    private final double weight;  // 가중치

    public Edge(String targetNode, double weight) {
        this.targetNode = targetNode;
        this.weight = weight;
    }

    public String getTargetNode() {  // 연결되는 노드 리턴
        return targetNode;
    }

    public double getWeight() {  // 가중치 리턴
        return weight;
    }
}
