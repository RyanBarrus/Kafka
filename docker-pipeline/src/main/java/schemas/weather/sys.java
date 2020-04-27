/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package schemas.weather;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class sys extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2984750518358078515L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"sys\",\"namespace\":\"schemas.weather\",\"fields\":[{\"name\":\"country\",\"type\":\"string\"},{\"name\":\"id\",\"type\":[\"null\",\"int\"],\"default\":null},{\"name\":\"message\",\"type\":[\"null\",\"float\"],\"default\":null},{\"name\":\"sunrise\",\"type\":\"int\"},{\"name\":\"sunset\",\"type\":\"int\"},{\"name\":\"type\",\"type\":[\"null\",\"int\"],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<sys> ENCODER =
      new BinaryMessageEncoder<sys>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<sys> DECODER =
      new BinaryMessageDecoder<sys>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<sys> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<sys> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<sys> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<sys>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this sys to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a sys from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a sys instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static sys fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence country;
   private java.lang.Integer id;
   private java.lang.Float message;
   private int sunrise;
   private int sunset;
   private java.lang.Integer type;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public sys() {}

  /**
   * All-args constructor.
   * @param country The new value for country
   * @param id The new value for id
   * @param message The new value for message
   * @param sunrise The new value for sunrise
   * @param sunset The new value for sunset
   * @param type The new value for type
   */
  public sys(java.lang.CharSequence country, java.lang.Integer id, java.lang.Float message, java.lang.Integer sunrise, java.lang.Integer sunset, java.lang.Integer type) {
    this.country = country;
    this.id = id;
    this.message = message;
    this.sunrise = sunrise;
    this.sunset = sunset;
    this.type = type;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return country;
    case 1: return id;
    case 2: return message;
    case 3: return sunrise;
    case 4: return sunset;
    case 5: return type;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: country = (java.lang.CharSequence)value$; break;
    case 1: id = (java.lang.Integer)value$; break;
    case 2: message = (java.lang.Float)value$; break;
    case 3: sunrise = (java.lang.Integer)value$; break;
    case 4: sunset = (java.lang.Integer)value$; break;
    case 5: type = (java.lang.Integer)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'country' field.
   * @return The value of the 'country' field.
   */
  public java.lang.CharSequence getCountry() {
    return country;
  }


  /**
   * Sets the value of the 'country' field.
   * @param value the value to set.
   */
  public void setCountry(java.lang.CharSequence value) {
    this.country = value;
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.lang.Integer getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.Integer value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'message' field.
   * @return The value of the 'message' field.
   */
  public java.lang.Float getMessage() {
    return message;
  }


  /**
   * Sets the value of the 'message' field.
   * @param value the value to set.
   */
  public void setMessage(java.lang.Float value) {
    this.message = value;
  }

  /**
   * Gets the value of the 'sunrise' field.
   * @return The value of the 'sunrise' field.
   */
  public int getSunrise() {
    return sunrise;
  }


  /**
   * Sets the value of the 'sunrise' field.
   * @param value the value to set.
   */
  public void setSunrise(int value) {
    this.sunrise = value;
  }

  /**
   * Gets the value of the 'sunset' field.
   * @return The value of the 'sunset' field.
   */
  public int getSunset() {
    return sunset;
  }


  /**
   * Sets the value of the 'sunset' field.
   * @param value the value to set.
   */
  public void setSunset(int value) {
    this.sunset = value;
  }

  /**
   * Gets the value of the 'type' field.
   * @return The value of the 'type' field.
   */
  public java.lang.Integer getType() {
    return type;
  }


  /**
   * Sets the value of the 'type' field.
   * @param value the value to set.
   */
  public void setType(java.lang.Integer value) {
    this.type = value;
  }

  /**
   * Creates a new sys RecordBuilder.
   * @return A new sys RecordBuilder
   */
  public static schemas.weather.sys.Builder newBuilder() {
    return new schemas.weather.sys.Builder();
  }

  /**
   * Creates a new sys RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new sys RecordBuilder
   */
  public static schemas.weather.sys.Builder newBuilder(schemas.weather.sys.Builder other) {
    if (other == null) {
      return new schemas.weather.sys.Builder();
    } else {
      return new schemas.weather.sys.Builder(other);
    }
  }

  /**
   * Creates a new sys RecordBuilder by copying an existing sys instance.
   * @param other The existing instance to copy.
   * @return A new sys RecordBuilder
   */
  public static schemas.weather.sys.Builder newBuilder(schemas.weather.sys other) {
    if (other == null) {
      return new schemas.weather.sys.Builder();
    } else {
      return new schemas.weather.sys.Builder(other);
    }
  }

  /**
   * RecordBuilder for sys instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<sys>
    implements org.apache.avro.data.RecordBuilder<sys> {

    private java.lang.CharSequence country;
    private java.lang.Integer id;
    private java.lang.Float message;
    private int sunrise;
    private int sunset;
    private java.lang.Integer type;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(schemas.weather.sys.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.country)) {
        this.country = data().deepCopy(fields()[0].schema(), other.country);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.id)) {
        this.id = data().deepCopy(fields()[1].schema(), other.id);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.message)) {
        this.message = data().deepCopy(fields()[2].schema(), other.message);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.sunrise)) {
        this.sunrise = data().deepCopy(fields()[3].schema(), other.sunrise);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.sunset)) {
        this.sunset = data().deepCopy(fields()[4].schema(), other.sunset);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.type)) {
        this.type = data().deepCopy(fields()[5].schema(), other.type);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
    }

    /**
     * Creates a Builder by copying an existing sys instance
     * @param other The existing instance to copy.
     */
    private Builder(schemas.weather.sys other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.country)) {
        this.country = data().deepCopy(fields()[0].schema(), other.country);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.id)) {
        this.id = data().deepCopy(fields()[1].schema(), other.id);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.message)) {
        this.message = data().deepCopy(fields()[2].schema(), other.message);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.sunrise)) {
        this.sunrise = data().deepCopy(fields()[3].schema(), other.sunrise);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.sunset)) {
        this.sunset = data().deepCopy(fields()[4].schema(), other.sunset);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.type)) {
        this.type = data().deepCopy(fields()[5].schema(), other.type);
        fieldSetFlags()[5] = true;
      }
    }

    /**
      * Gets the value of the 'country' field.
      * @return The value.
      */
    public java.lang.CharSequence getCountry() {
      return country;
    }


    /**
      * Sets the value of the 'country' field.
      * @param value The value of 'country'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setCountry(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.country = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'country' field has been set.
      * @return True if the 'country' field has been set, false otherwise.
      */
    public boolean hasCountry() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'country' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearCountry() {
      country = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.lang.Integer getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setId(java.lang.Integer value) {
      validate(fields()[1], value);
      this.id = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearId() {
      id = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'message' field.
      * @return The value.
      */
    public java.lang.Float getMessage() {
      return message;
    }


    /**
      * Sets the value of the 'message' field.
      * @param value The value of 'message'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setMessage(java.lang.Float value) {
      validate(fields()[2], value);
      this.message = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'message' field has been set.
      * @return True if the 'message' field has been set, false otherwise.
      */
    public boolean hasMessage() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'message' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearMessage() {
      message = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'sunrise' field.
      * @return The value.
      */
    public int getSunrise() {
      return sunrise;
    }


    /**
      * Sets the value of the 'sunrise' field.
      * @param value The value of 'sunrise'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setSunrise(int value) {
      validate(fields()[3], value);
      this.sunrise = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'sunrise' field has been set.
      * @return True if the 'sunrise' field has been set, false otherwise.
      */
    public boolean hasSunrise() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'sunrise' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearSunrise() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'sunset' field.
      * @return The value.
      */
    public int getSunset() {
      return sunset;
    }


    /**
      * Sets the value of the 'sunset' field.
      * @param value The value of 'sunset'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setSunset(int value) {
      validate(fields()[4], value);
      this.sunset = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'sunset' field has been set.
      * @return True if the 'sunset' field has been set, false otherwise.
      */
    public boolean hasSunset() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'sunset' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearSunset() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'type' field.
      * @return The value.
      */
    public java.lang.Integer getType() {
      return type;
    }


    /**
      * Sets the value of the 'type' field.
      * @param value The value of 'type'.
      * @return This builder.
      */
    public schemas.weather.sys.Builder setType(java.lang.Integer value) {
      validate(fields()[5], value);
      this.type = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'type' field has been set.
      * @return True if the 'type' field has been set, false otherwise.
      */
    public boolean hasType() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'type' field.
      * @return This builder.
      */
    public schemas.weather.sys.Builder clearType() {
      type = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public sys build() {
      try {
        sys record = new sys();
        record.country = fieldSetFlags()[0] ? this.country : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.id = fieldSetFlags()[1] ? this.id : (java.lang.Integer) defaultValue(fields()[1]);
        record.message = fieldSetFlags()[2] ? this.message : (java.lang.Float) defaultValue(fields()[2]);
        record.sunrise = fieldSetFlags()[3] ? this.sunrise : (java.lang.Integer) defaultValue(fields()[3]);
        record.sunset = fieldSetFlags()[4] ? this.sunset : (java.lang.Integer) defaultValue(fields()[4]);
        record.type = fieldSetFlags()[5] ? this.type : (java.lang.Integer) defaultValue(fields()[5]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<sys>
    WRITER$ = (org.apache.avro.io.DatumWriter<sys>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<sys>
    READER$ = (org.apache.avro.io.DatumReader<sys>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.country);

    if (this.id == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeInt(this.id);
    }

    if (this.message == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeFloat(this.message);
    }

    out.writeInt(this.sunrise);

    out.writeInt(this.sunset);

    if (this.type == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeInt(this.type);
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.country = in.readString(this.country instanceof Utf8 ? (Utf8)this.country : null);

      if (in.readIndex() != 1) {
        in.readNull();
        this.id = null;
      } else {
        this.id = in.readInt();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.message = null;
      } else {
        this.message = in.readFloat();
      }

      this.sunrise = in.readInt();

      this.sunset = in.readInt();

      if (in.readIndex() != 1) {
        in.readNull();
        this.type = null;
      } else {
        this.type = in.readInt();
      }

    } else {
      for (int i = 0; i < 6; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.country = in.readString(this.country instanceof Utf8 ? (Utf8)this.country : null);
          break;

        case 1:
          if (in.readIndex() != 1) {
            in.readNull();
            this.id = null;
          } else {
            this.id = in.readInt();
          }
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.message = null;
          } else {
            this.message = in.readFloat();
          }
          break;

        case 3:
          this.sunrise = in.readInt();
          break;

        case 4:
          this.sunset = in.readInt();
          break;

        case 5:
          if (in.readIndex() != 1) {
            in.readNull();
            this.type = null;
          } else {
            this.type = in.readInt();
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









