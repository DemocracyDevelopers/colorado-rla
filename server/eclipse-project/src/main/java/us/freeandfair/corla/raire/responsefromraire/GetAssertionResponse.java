/*
  Copyright 2023 Democracy Developers
  This is a Java re-implementation of raire-rs https://github.com/DemocracyDevelopers/raire-rs
  It attempts to copy the design, API, and naming as much as possible subject to being idiomatic and efficient Java.

  This file is part of raire-java.
  raire-java is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
  raire-java is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
  You should have received a copy of the GNU Affero General Public License along with ConcreteSTV.  If not, see <https://www.gnu.org/licenses/>.

 */

package us.freeandfair.corla.raire.responsefromraire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.beans.ConstructorProperties;
import java.util.Map;

/*
 * Data structure to interpret the assertions from RAIRE.
 * Same top-level structures as raire-service/response/GetAssertionResponse
 * but only a string instead of the detailed structures of the RAIRE response. This is because that string is simply
 * exported as a json string without needing to be understood.
 */

public class GetAssertionResponse {

    /// A wrapper around the raire-java Error type. Exactly one of the fields will be null.
    public final Map<String,Object> metadata;
    public final GetAssertionResultOrError solution;

    @ConstructorProperties({"metadata","solution"})
    public GetAssertionResponse(Map<String,Object> metadata, GetAssertionResultOrError solutionOrError) {
        this.metadata = metadata;
        this.solution = solutionOrError;
    }

    public static class GetAssertionResultOrError {

        // The JsonNode here is actually a raire-service::RetrievedRaireResult, but we're just outputting it as a json string.
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public final JsonNode Ok;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public final GetAssertionError Err;

        /** Only used by the Jackson serialization which can only have one constructor annotated :-( */
        @ConstructorProperties({"Ok","Err"})
        public GetAssertionResultOrError(JsonNode Ok, GetAssertionError Err) { this.Ok=Ok; this.Err=Err;}
        public GetAssertionResultOrError(JsonNode Ok) { this.Ok=Ok; this.Err=null;}
        public GetAssertionResultOrError(GetAssertionError Err) { this.Ok=null; this.Err=Err;}
    }

}