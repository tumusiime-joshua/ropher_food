package com.ropherfood.client;

public class MarketsData {

    public String marketId;
    public String marketName;

    public MarketsData() {
    }

    public MarketsData(String marketId, String marketName) {
        this.marketId = marketId;
        this.marketName = marketName;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
}
