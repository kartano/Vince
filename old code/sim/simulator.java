package sim;
import gui.*;
import java.lang.Thread;

/**
 * <p>Title: Simulator </p>
 * <p>Description: Time-based simulator for VinceComponents  </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */



public class simulator extends Thread
{
  private WorkSpace workspace;
  private int step_interval;
  private boolean flatout = false;
  private int time_period = 500;
  private boolean running = false;
  private int limit = 10; // limit for vomit style execution
  private boolean is_running = false;

  public simulator(WorkSpace workspace)
  {
    this.workspace = workspace;
  }

  public void run()
  {
    try
    {
      while (true)
      {
        if (running)
        {
          while(flatout)
          {
            workspace.updateInputs();
            workspace.updateComponents();
            workspace.updateOutputs();
            workspace.updateWires();
          }
        }
        else
        {
          this.sleep(0);
        }
      }
    }
    catch(InterruptedException IEx)
    {
      System.out.println("Sleep Thread");
      IEx.printStackTrace();
    }
  }

  public void go()
  {
      this.startSimulator();
      flatout = true;
  }

  public void step()
  {
    this.startSimulator();
    for(int i = 0;i < step_interval; i++)
    {
      workspace.updateInputs();
      workspace.updateComponents();
      workspace.updateOutputs();
      workspace.updateWires();
      this.yield();
    }
    this.stopSimulator();
  }

  public void startSimulator()
  {
    running = true;
  }

  public void stopSimulator()
  {
    running = false;
  }

  public void setStep(int step)
  {
    step_interval = step;
  }
}