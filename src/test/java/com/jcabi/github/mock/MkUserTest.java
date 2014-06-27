/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jcabi.github.mock;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import com.jcabi.github.Organizations;

/**
 *
 * @author ed
 */
public final class MkUserTest {

    @Test
    public void testGetOrganizations() throws IOException {
        final MkUser user = new MkUser(
            new MkStorage.InFile(),
            "orgTestIterate"
        );
        final Organizations orgs = user.organizations();
        Assert.assertNotNull(orgs);
    }
}
