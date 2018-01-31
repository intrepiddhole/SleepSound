package ipnossoft.rma.deepurl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DeepUrlAction {
  private HashMap<String, List<String>> params;

  DeepUrlAction(HashMap<String, List<String>> var1) {
    this.params = var1;
  }

  List<String> getParameter(String var1) {
    List var2 = (List)this.params.get(var1);
    Object var3 = var2;
    if(var2 == null) {
      var3 = new ArrayList();
    }

    return (List)var3;
  }

  public abstract void run();
}
