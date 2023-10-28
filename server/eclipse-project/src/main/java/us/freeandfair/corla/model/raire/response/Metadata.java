package us.freeandfair.corla.model.raire.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// For RAIRE, metadata can be anything, but for us it's only going to be a list of Candidate names and an optional note.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    List<String> candidates;
}
