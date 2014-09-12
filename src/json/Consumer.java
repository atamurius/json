package json;

public interface Consumer {

	void onNull();
	void onString(String value);
	void onNumber(Number value);
	void onBoolean(boolean value);
	
	void beforeList();
	void beforeListItem();
	void afterListItem();
	void afterList();
	
	void beforeObject();
	void beforeProperty(String name);
	void afterProperty(String name);
	void afterObject();
}
