package json.io;

import java.io.Reader;

import json.Producer;

public interface Parser {

	Producer parse(Reader in);
}
