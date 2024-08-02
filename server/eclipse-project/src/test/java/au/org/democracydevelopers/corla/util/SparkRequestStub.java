/*
Democracy Developers IRV extensions to colorado-rla.

@copyright 2024 Colorado Department of State

These IRV extensions are designed to connect to a running instance of the raire
service (https://github.com/DemocracyDevelopers/raire-service), in order to
generate assertions that can be audited using colorado-rla.

The colorado-rla IRV extensions are free software: you can redistribute it and/or modify it under the terms
of the GNU Affero General Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

The colorado-rla IRV extensions are distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License along with
raire-service. If not, see <https://www.gnu.org/licenses/>.
*/

package au.org.democracydevelopers.corla.util;

import spark.Request;

import java.util.Set;

/**
 * A class that can behave like a Spark request, for testing endpoints.
 * Note this does _not_ (yet) implement all the functions you might need - it contains only the ones
 * that I noticed being used in colorado-rla.
 * https://javadoc.io/doc/com.sparkjava/spark-core/2.5.4/spark/Request.html
 */
public class SparkRequestStub extends Request {

    private final String _body;
    private final Set<String> _queryParams;

    public SparkRequestStub(final String body, final Set<String> queryParams)
    {
      super();

      _body = body;
      _queryParams = queryParams;
    }

    public String body()
    {
        return _body;
    }
    public String host() { return "Test host";}
    public Set<String> queryParams() { return _queryParams;}
}
