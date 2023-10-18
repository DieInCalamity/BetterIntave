package de.jpx3.intave.connect.cloud.protocol.packets;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.jpx3.intave.annotate.KeepEnumInternalNames;
import de.jpx3.intave.connect.cloud.protocol.Direction;
import de.jpx3.intave.connect.cloud.protocol.Identity;
import de.jpx3.intave.connect.cloud.protocol.JsonPacket;
import de.jpx3.intave.connect.cloud.protocol.listener.Clientbound;

public final class ClientboundSampleTransmissionAcknowledgement extends JsonPacket<Clientbound> {
	private Identity identity;
	private AcceptedState state = AcceptedState.ACCEPTED;
	private Classification classification;

	public ClientboundSampleTransmissionAcknowledgement() {
		super(Direction.CLIENTBOUND, "SAMPLE_TRANSMISSION_ACKNOWLEDGEMENT", "1");
	}

	@Override
	public void serialize(JsonWriter writer) {
		try {
			writer.beginObject();
			writer.name("id");
			identity.serialize(writer);
			writer.name("state");
			writer.value(state.name());
			writer.name("classification");
			writer.value(classification.name());
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
					case "state":
						state = AcceptedState.valueOf(reader.nextString());
						break;
					case "classification":
						classification = Classification.valueOf(reader.nextString());
						break;
				}
			}
			reader.endObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@KeepEnumInternalNames
	public enum AcceptedState {
		ACCEPTED,
		REJECTED
	}

	@KeepEnumInternalNames
	public enum Classification {
		LEGIT, CHEAT,
		UNKNOWN
	}
}
