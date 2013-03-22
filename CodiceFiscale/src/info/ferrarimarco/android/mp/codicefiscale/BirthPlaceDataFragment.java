package info.ferrarimarco.android.mp.codicefiscale;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BirthPlaceDataFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.birth_place_data_fragment_layout, container, false);
		
		return view;
	}
	
}
