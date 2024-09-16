package me.ezik.shared.message;

public enum MessageType {
	TEST(-1),
	AUTH(1),
	REG(2),
	SEARCH_GAMES(3),
	ERROR(4),
	GET_GAME(5),
	CREATE_GAME(6),
	JOIN_GAME(7),
	LEAVE_GAME(8),
	SAVE_SETTINGS(9),
	SAVE_AS_DEFAULT(10),
	KICK(11),
	CHANGE_PFP(12),
	START_GAME(13),
	LOAD_MAP(14),
	GET_COMPANY(15),
	SEND_MESSAGE(16),
	ROLL(17),
	START_TURN(18),
	TIMEOUT(19),
	UPDATE_TIMER(20),
	DEBUG(21),
	CONFIRM_EVENT(22),
	UPDATE_MONEY(23),
	CONFIRM_BUY(24),
	UPDATE_CARD(25),
	AUCTION(26),
	AUCTION_YES(27),
	AUCTION_NO(28),
	SEND_SYSTEM_MESSAGE(29),
	CASINO_AGREE(30),
	SKIP_TURN(31),
	JAIL_START(32),
	JAIL_PAY(33),
	UPDATE_CHIP(34),
	UPGRADE_CARD(35),
	SELL_UPGRADE(36),
	LAYOUT_CARD(37),
	DELAYOUT_CARD(38),
	DRAW_LAYOUT(39),
	TRADE_OFFER(40),
	TRADE_ACCEPT(41),
	TRADE_DECLINE(42),
	SURRENDER(43),
	WIN(44),
	CLEAR_TRADE(45),
	CLEAR_MENU(46),
	SEND_DELAYED_MESSAGE(47),
	ROLLBACK_PANEL(48),
	KEEP_ALIVE(49);
	
	int code;
	
	private MessageType(int id) {
		this.code = id;
	}
	
	public static MessageType getById(int id) {
	    for(MessageType e : values()) {
	        if(e.code == id) return e;
	    }
	    return TEST;
	}
}
