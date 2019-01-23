package com.ryuhi.util.dubbo.test.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class BaseController {

    protected static Logger log = LogManager.getLogger(new Exception().getStackTrace()[1].getClassName());
}
