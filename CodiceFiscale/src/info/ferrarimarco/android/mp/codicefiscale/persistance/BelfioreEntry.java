package info.ferrarimarco.android.mp.codicefiscale.persistance;


public class BelfioreEntry {

	public int id;
	public String idNazionale;
	public String provincia;
	public String comune;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getIdNazionale() {
		return idNazionale;
	}
	
	public void setIdNazionale(String idNazionale) {
		this.idNazionale = idNazionale;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	public String getComune() {
		return comune;
	}
	
	public void setComune(String comune) {
		this.comune = comune;
	}
	
	@Override
	public String toString(){
		return idNazionale;
	}
	
}
