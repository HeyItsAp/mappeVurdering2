package ntnu.gruppe21;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {
  private List<Share> shares;

  public Portfolio() {
    this.shares = new ArrayList<>();
  }

  public void addShare(Share share) {
    shares.add(share);
  }

  public void removeShare(Share share) {
    shares.remove(share);
  }

  public List<Share> getShares() {
    return shares;
  }

  public boolean containsShare(Share share) {
    return shares.contains(share);
  }
}
