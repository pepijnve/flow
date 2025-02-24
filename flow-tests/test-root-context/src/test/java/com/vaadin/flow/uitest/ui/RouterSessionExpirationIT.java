/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.uitest.ui;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.testutil.ChromeBrowserTest;

public class RouterSessionExpirationIT extends ChromeBrowserTest {

    @Override
    protected String getTestPath() {
        return "/view/";
    }

    @Test
    public void should_HaveANewSessionId_when_NavigationAfterSessionExpired() {
        openUrl("/new-router-session/NormalView");

        navigateToAnotherView();
        String sessionId = getSessionId();
        navigateToFirstView();
        Assert.assertEquals(sessionId, getSessionId());

        navigateToSesssionExpireView();
        // expired session causes page reload, after the page reload there will
        // be a new session
        Assert.assertNotEquals(sessionId, getSessionId());
        sessionId = getSessionId();
        navigateToAnotherView();
        // session is preserved
        Assert.assertEquals(sessionId, getSessionId());
    }

    @Test
    @Ignore("Ignored because of fusion issue : https://github.com/vaadin/flow/issues/7581")
    public void should_StayOnSessionExpirationView_when_NavigationAfterSessionExpired() {
        openUrl("/new-router-session/NormalView");

        navigateToSesssionExpireView();

        assertTextAvailableInView("ViewWhichInvalidatesSession");
    }

    @Test
    public void navigationAfterInternalError() {
        openUrl("/new-router-session/NormalView");

        navigateToAnotherView();
        String sessionId = getSessionId();
        navigateToInternalErrorView();

        waitUntil(webDriver -> findElements(By.id("sessionId")).isEmpty());
        // Navigate back as we are on the error view.
        getDriver().navigate().back();
        Assert.assertEquals(sessionId, getSessionId());
    }

    private String getSessionId() {
        return findElement(By.id("sessionId")).getText();
    }

    private void navigateToFirstView() {
        navigateTo("NormalView");
    }

    private void navigateToAnotherView() {
        navigateTo("AnotherNormalView");
    }

    private void navigateToSesssionExpireView() {
        findElement(By.linkText("ViewWhichInvalidatesSession")).click();
    }

    private void navigateToInternalErrorView() {
        findElement(By.linkText("ViewWhichCausesInternalException")).click();
        // Won't actually reach the view..
    }

    private void navigateTo(String linkText) {
        findElement(By.linkText(linkText)).click();
        assertTextAvailableInView(linkText);

    }

    private void assertTextAvailableInView(String linkText) {
        Assert.assertNotNull(
                findElement(By.xpath("//strong[text()='" + linkText + "']")));
    }
}
