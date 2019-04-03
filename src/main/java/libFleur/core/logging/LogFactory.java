package libFleur.core.logging;

import java.util.logging.Logger;

public class LogFactory {
  
  private LogFactory(){}
  
  public static Logger createLogger(String sourceName){  
    return Logger.getLogger(sourceName);
  }
}
