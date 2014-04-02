package Provider;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class XMLDocumentProvider extends ContentProvider{
 
	private static final String TAG = "XmlDocumentProvider";
	public static final Uri OSM_CONTENT_URI = Uri.parse("content://com.Provider.provider/OSMDocs");
	public static final Uri POI_CONTENT_URI = Uri.parse("content://com.Provider.provider/POIDATAS");


	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		 throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 throw new UnsupportedOperationException();
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
				return null;

	}

	
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		 throw new UnsupportedOperationException();
	}

	
	  public AssetFileDescriptor openAssetFile(Uri uri, String mode, String xmlFile) throws FileNotFoundException {
		Log.d(TAG, "Uri: " + uri);
		Log.d(TAG, "mode: " + mode);
		try {
		    return getContext().getAssets().openFd(xmlFile);
		}
		catch (IOException e) {
		    e.printStackTrace();
		    Log.e(TAG, "ERROR: " + e);
		    throw new FileNotFoundException(e.getMessage());
		}
	    }
}
