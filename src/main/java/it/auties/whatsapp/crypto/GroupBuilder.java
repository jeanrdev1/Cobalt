package it.auties.whatsapp.crypto;

import it.auties.whatsapp.controller.Keys;
import it.auties.whatsapp.model.signal.keypair.SignalKeyPair;
import it.auties.whatsapp.model.signal.message.SignalDistributionMessage;
import it.auties.whatsapp.model.signal.sender.SenderKeyName;
import it.auties.whatsapp.util.KeyHelper;

public record GroupBuilder(Keys keys) {
    public byte[] createOutgoing(SenderKeyName name) {
        var record = keys.findSenderKeyByName(name);
        if (record.isEmpty()) {
            record.addState(KeyHelper.senderKeyId(), SignalKeyPair.random(), 0, KeyHelper.senderKey());
        }
        var state = record.firstState();
        var message = new SignalDistributionMessage(state.id(), state.chainKey().iteration(), state.chainKey().seed(), state.signingKey().encodedPublicKey());
        return message.serialized();
    }

    public void createIncoming(SenderKeyName name, SignalDistributionMessage message) {
        var record = keys.findSenderKeyByName(name);
        record.addState(message.id(), message.signingKey(), message.iteration(), message.chainKey());
    }
}
