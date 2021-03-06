package edu.mit.cameraCulture.vblocks.predefined.augmenter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import edu.mit.cameraCulture.vblocks.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileChooserDialog extends Dialog {

	private FolderLayout mLayout = null; 
	
	public FileChooserDialog(Context context) {
		super(context);
		this.setContentView(mLayout = new FolderLayout(context));
		this.setTitle("Choose a model");
	}
	
    public interface IFolderItemListener {

        void OnCannotFileRead(File file);//implement what to do folder is Unreadable
        void OnFileSelected(String file);//What to do When a file is clicked
    }
    
    public void setIFolderItemListener(IFolderItemListener folderItemListener) {
        mLayout.setIFolderItemListener(folderItemListener);
    }
	
	public class FolderLayout extends LinearLayout implements OnItemClickListener {

        Context context;
        IFolderItemListener folderListener;
        private List<String> item = null;
        private List<String> path = null;
        private String root = "/";
        private TextView myPath;
        private ListView lstView;

        public FolderLayout(Context context) {
            super(context);

            // TODO Auto-generated constructor stub
            this.context = context;


            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(VERTICAL);
            
            this.addView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            
            //LayoutInflater layoutInflater = (LayoutInflater) context
            //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //View view = layoutInflater.inflate(R.layout.folderview, this);

            myPath = new TextView(context);//(TextView) findViewById(R.id.path);
            layout.addView(myPath,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            
            lstView = new ListView(context); // (ListView) findViewById(R.id.list);
            layout.addView(lstView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            
            Log.i("FolderView", "Constructed");
            getDir(root, lstView);

        }

        public void setIFolderItemListener(IFolderItemListener folderItemListener) {
            this.folderListener = folderItemListener;
        }

        //Set Directory for view at anytime
        public void setDir(String dirPath){
            getDir(dirPath, lstView);
        }


        private void getDir(String dirPath, ListView v) {

            myPath.setText("Location: " + dirPath);
            item = new ArrayList<String>();
            path = new ArrayList<String>();
            File f = new File(dirPath);
            File[] files = f.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return (pathname.isDirectory() || pathname.getName().endsWith(".obj") || pathname.getName().endsWith(".3ds"));
				}
			});

            if (!dirPath.equals(root)) {

                item.add(root);
                path.add(root);
                item.add("../");
                path.add(f.getParent());

            }	
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                path.add(file.getPath());
                if (file.isDirectory())
                    item.add(file.getName() + "/");
                else
                    item.add(file.getName());

            }

            Log.i("Folders", files.length + "");

            setItemList(item);

        }

        //can manually set Item to display, if u want
        public void setItemList(List<String> item){
            ArrayAdapter<String> fileList = new ArrayAdapter<String>(context,
                    R.layout.row_view, item);

            lstView.setAdapter(fileList);
            
            lstView.setOnItemClickListener(this);
        }


        public void onListItemClick(ListView l, View v, int position, long id) {
            final File file = new File(path.get(position));
            if (file.isDirectory()) {
                if (file.canRead())
                    getDir(path.get(position), l);
                else {
                	//what to do when folder is unreadable
              //      if (folderListener != null) {
              //          folderListener.OnCannotFileRead(file);

              //      }

                }
            } else {
            	
            	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
        				context);
         
        			// set title
        			alertDialogBuilder.setTitle("Select file?");
         
        			// set dialog message
        			alertDialogBuilder
        				.setMessage("Do you wan to load "+ file.getName() +"? ")
        				.setCancelable(false)
        				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {
        						// if this button is clicked, close
        						// current activity
        						FileChooserDialog.this.dismiss();
        						//mSelectedFile = file.getAbsolutePath();
        						if (folderListener != null) {
        		                    folderListener.OnFileSelected(file.getAbsolutePath());
        		                }
        					}
        				  })
        				.setNegativeButton("No",new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog,int id) {
        						// if this button is clicked, just close
        						// the dialog box and do nothing
        						dialog.cancel();
        					}
        					
        				});
        			// create alert dialog
    				AlertDialog alertDialog = alertDialogBuilder.create();
     
    				// show it
    				alertDialog.show();
        			
            	
            	//what to do when file is clicked
            	//You can add more,like checking extension,and performing separate actions
                

            }
        }

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            onListItemClick((ListView) arg0, arg0, arg2, arg3);
        }

    }

}
