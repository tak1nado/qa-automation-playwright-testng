package com.project.qa.steps;

import com.project.qa.helpers.ThreadVarsHashMap;
import com.project.qa.helpers.user.engine.UserSessions;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStepDefs {
    @Autowired protected ThreadVarsHashMap<TestKeyword> threadVarsHashMap;
    @Autowired protected UserSessions userSessions;

}