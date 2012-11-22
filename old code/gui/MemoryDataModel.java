package gui;
import javax.swing.table.*;

/**
 * <p>Title: Memory Data Model </p>
 * <p>Description: Stores memory data for display in memory frame</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Craig Eales
 * @version 1.0
 */

public class MemoryDataModel extends AbstractTableModel
{
  String[] dataArray;
  private int number_of_columns;
// array of memory bytes as strings, and number of columns to be output
  public MemoryDataModel(String[] data,int col)
  {
    number_of_columns = col;// number of columns of data, does not include address column
    dataArray = new String[data.length + data.length/number_of_columns];
    for(int i=0;i<(data.length/number_of_columns);i++)
   {
     setValueAt(Integer.toHexString(i*number_of_columns),i,0);
     for(int j = 0;j<number_of_columns;j++)
     {
       setValueAt((data[(number_of_columns*i) +j]), i, j+1);
     }
   }
  }

  public int getColumnCount() { return number_of_columns+1; }
  public int getRowCount() { return dataArray.length/(number_of_columns+1);}
  public Object getValueAt(int row, int col)
  {
    return dataArray[(row*(number_of_columns+1))+col];
  }

  public void setValueAt(String i,int rowIndex,int columnIndex)
  {
    dataArray[(rowIndex*(number_of_columns+1)) + columnIndex]=i;
  }

      public String getColumnName(int columnIndex)
      {
            switch (columnIndex)
            {
                  case 0 : return "Location";
                  case 1 : return "Value";
                  default : return "";
            }
      }

}
