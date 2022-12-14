package lab.end2end.concert.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import javax.persistence.*;
import lab.end2end.concert.jackson.LocalDateTimeDeserializer;
import lab.end2end.concert.jackson.LocalDateTimeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class to represent a Concert. A Concert is characterised by an unique ID,
 * title, date and time, and a featuring Performer.
 * <p>
 * Concert implements Comparable with a natural ordering based on its title.
 * Hence, in a List, Concert instances can be sorted into alphabetical order
 * based on their title value.
 */
//TODO: add annotation for entity
@Entity
public class Concert implements Comparable<Concert> {

  //TODO: add annotation for id
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private LocalDateTime date;

  //TODO: add annotation for relationship
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Performer performer;

  public Concert(
    Long id,
    String title,
    LocalDateTime date,
    Performer performer
  ) {
    this.id = id;
    this.title = title;
    this.date = date;
    this.performer = performer;
  }

  public Concert(String title, LocalDateTime date, Performer performer) {
    this(null, title, date, performer);
  }

  public Concert() {}

  public Concert(Performer performer) {
    this.performer = performer;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  //TODO: add the JSONSerialze and JsonDeserialize
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Performer getPerformer() {
    return performer;
  }

  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Concert, id: ");
    buffer.append(id);
    buffer.append(", title: ");
    buffer.append(title);
    buffer.append(", date: ");
    buffer.append(date.toString());
    buffer.append(", featuring: ");
    buffer.append(performer.getName());

    return buffer.toString();
  }

  @Override
  public boolean equals(Object obj) {
    // Implement value-equality based on a Concert's title alone. ID isn't
    // included in the equality check because two Concert objects could
    // represent the same real-world Concert, where one is stored in the
    // database (and therefore has an ID - a primary key) and the other
    // doesn't (it exists only in memory).
    if (!(obj instanceof Concert)) return false;
    if (obj == this) return true;

    Concert rhs = (Concert) obj;
    return new EqualsBuilder().append(title, rhs.title).isEquals();
  }

  @Override
  public int hashCode() {
    // Hash-code value is derived from the value of the title field. It's
    // good practice for the hash code to be generated based on a value
    // that doesn't change.
    return new HashCodeBuilder(17, 31).append(title).hashCode();
  }

  @Override
  public int compareTo(Concert concert) {
    return title.compareTo(concert.getTitle());
  }
}
