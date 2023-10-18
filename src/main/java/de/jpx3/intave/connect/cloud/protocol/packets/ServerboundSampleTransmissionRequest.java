package de.jpx3.intave.connect.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.connect.cloud.protocol.Direction;
import de.jpx3.intave.connect.cloud.protocol.Identity;
import de.jpx3.intave.connect.cloud.protocol.JsonPacket;
import de.jpx3.intave.connect.cloud.protocol.listener.Serverbound;

public final class ServerboundSampleTransmissionRequest extends JsonPacket<Serverbound> {
	private Identity identity;

	public ServerboundSampleTransmissionRequest() {
		super(Direction.SERVERBOUND, "SAMPLE_TRANSMISSION_REQUEST", "1");
	}

	public ServerboundSampleTransmissionRequest(Identity identity) {
		this();
		this.identity = identity;
	}

	public Identity identity() {
		return identity;
	}

	@Override
	public void serialize(JsonWriter writer) {
		try {
			writer.beginObject();
			writer.name("id");
			identity.serialize(writer);
			writer.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deserialize(JsonReader reader) {
		try {
			reader.beginObject();
			while (reader.hasNext()) {
				switch (reader.nextName()) {
					case "id":
						identity = Identity.from(reader);
						break;
				}
			}
			reader.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
