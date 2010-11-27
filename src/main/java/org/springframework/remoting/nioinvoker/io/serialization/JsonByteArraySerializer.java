package org.springframework.remoting.nioinvoker.io.serialization;

import hr.sting.niocommunicator.serialization.ByteArraySerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * POJO <-> JSON (de)serializer.
 * Serializes objects to JSON object representations using <a href="http://jackson.codehaus.org/">Jackson</a> library.
 * Deserializes JSON object representations using <a href="http://jackson.codehaus.org/">Jackson</a> library.
 *
 * @author mstipanov
 * @see org.codehaus.jackson.map.ObjectMapper, Serializer, SerializationException
 * @since 16.03.2010. 10:29:36
 */
public class JsonByteArraySerializer implements ByteArraySerializer {
    private static final Log LOG = LogFactory.getLog(JsonByteArraySerializer.class);

    private ObjectMapper mapper;

    private ObjectMapper getObjectMapper() {
        if (null != mapper) {
            return mapper;
        }

        mapper = new ObjectMapper();

        mapper.getSerializationConfig().enable(SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        mapper.getSerializationConfig().enable(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        return mapper;
    }

    /**
     * Deserializes JSON object representations using {@link org.codehaus.jackson.map.ObjectMapper}.
     * When an java.io.IOException happens it throws org.infobip.common.io.SerializationException
     *
     * @param bytes  JSON object
     * @param aClass Target object type
     * @return deserialized object of type requested by parameter aClass
     * @throws java.io.IOException on deserialization error
     * @see org.codehaus.jackson.map.ObjectMapper
     * @see java.io.IOException
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> aClass) throws IOException {
        if (LOG.isDebugEnabled())
            LOG.debug("Deserializing: " + new String(bytes, "UTF-8"));

        ObjectMapper objectMapper = getObjectMapper();
        Object value = objectMapper.readValue(bytes, 0, bytes.length, Object.class);

        if (LOG.isDebugEnabled())
            LOG.debug("Deserialized: " + value);

        return (T) value;
    }

    /**
     * Serializes objects to JSON object representations using {@link org.codehaus.jackson.map.ObjectMapper}.
     * When an java.io.IOException happens it throws org.infobip.common.io.SerializationException
     *
     * @param o serialization source
     * @return JSON object
     * @throws java.io.IOException on deserialization error
     * @see org.codehaus.jackson.map.ObjectMapper
     * @see java.io.IOException
     */
    @Override
    public byte[] serialize(Object o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        getObjectMapper().writeValue(outputStream, o);
        return outputStream.toByteArray();
    }
}
