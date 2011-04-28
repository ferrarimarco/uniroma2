package simulator;

public enum TipoEvento {
	FINECPU(0),
	FINEDISK(1),
	FINETERM1(2),
	FINETERM2(3),
	FINETERM3(4),
	FINETERM4(5),
	FINETERM5(6),
	FINETERM6(7),
	FINETERM7(8),
	FINETERM8(9),
	FINETERM9(10),
	FINETERM10(11),
	FINETERM11(12),
	FINETERM12(13),
	FINEHOST1(14),
	FINEHOST2(15),
	FINEHOST3(16),
	FINEHOST4(17),
	FINEHOST5(18),
	FINEHOST6(19),
	FINEHOST7(20),
	FINEHOST8(21),
	FINEHOST9(22),
	FINEHOST10(23),
	FINEHOST11(24),
	FINEHOST12(25),
	FINEST1(26),
	FINEST2(27),
	FINEST3(28),
	FINEST4(29),
	FINEST5(30),
	FINEST6(31),
	FINEST7(32),
	FINEST8(33),
	FINEST9(34),
	FINEST10(35),
	FINEST11(36),
	FINEST12(37);
	
	private final int eventId;
	
	TipoEvento(int eventId){
		this.eventId = eventId;
	}

	public int getEventId() {
		return eventId;
	}	
}
