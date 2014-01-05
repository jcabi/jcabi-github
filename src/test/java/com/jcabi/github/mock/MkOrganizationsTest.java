/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.github.mock;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import com.jcabi.github.Organization;
import com.jcabi.github.Organizations;

/**
 * Github organizations.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @see <a href="http://developer.github.com/v3/orgs/">Organizations API</a>
 * @since 0.7
 */
public class MkOrganizationsTest {
    /**
     * MkOrganizations can list organizations.
     * @throws Exception If some problem inside
     */
    @Test
    public void iteratesOrganizations() throws Exception {
      Organizations orgs = getOrganizations();
    }

    /**
     * MkOrganizations can get specific organization.
     * @throws Exception If some problem inside
     */
    @Test
    public void getSingleOrganization() throws Exception {
      Organizations orgs = getOrganizations();
      //get the first org, which we already now the orgs isn't empty
      Organization org = orgs.iterate().iterator().next();
      //assume the org isnt null
      Assume.assumeThat(org, Matchers.notNullValue());
      //make sure we have a valid id
      MatcherAssert.assertThat(org.orgId(), Matchers.greaterThan(0));
      //get the org via the get method
      Organization o = orgs.get(org.orgId());
      //assert they are the same
      MatcherAssert.assertThat(org.orgId(), Matchers.equalTo(o.orgId()));
    }

    /**
     * @return a mock organizations object
     */
    private static Organizations getOrganizations() throws Exception
    {
      //create a new mock organizations object
      final Organizations orgs = new MkOrganizations(
        new MkStorage.InFile(),"login-less"
      );
      //put a dummy object into the orgs
      Organization o = orgs.get(1);
      //this should pretty much be impossible since we just added an org
      MatcherAssert.assertThat(
          orgs.iterate(),
          Matchers.not(Matchers.emptyIterable())
      );
      //make sure we have a valid user
      MatcherAssert.assertThat(orgs.user(), Matchers.notNullValue());
      return orgs;
    }
}
