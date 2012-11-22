package vincecomponents;

/**
 * <p>Title: Pin </p>
 * <p>Description: Pin component for VinceGUI </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class Pin
{
  // const or final???
  private byte pin;
  private Wire wire = null;
  private int state;
  private int pinNumber;
  private int default_state;
  private VinceGUIComponent component;
  private final byte INPUT = 0;
  private final byte OUTPUT = 1;

  public Pin(VinceGUIComponent comp, int number, byte pintype, int default_s)
  {
    component = comp;
    pin = pintype;
    pinNumber=number;
    default_state = default_s;
  }

  public void updateState()
  {
    if(pin==INPUT)
    {
      if(wire!=null)
      {
        state = wire.getState();
      }
      else
      {
        state = default_state;
      }
      return;
    }
    if(pin==OUTPUT)
    {
      state = component.getState(pinNumber);
      return;
    }
    //error here
    System.exit(1);
  }
}