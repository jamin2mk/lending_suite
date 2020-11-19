package com.helper;

import com.common.Const;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ErrorUtil {

	public static JsonElement getSuccessInfo() {

		JsonObject jErrObject = new JsonObject();

		jErrObject.addProperty(Const.ERROR_CODE, Const.SUCCESS_CODE);
		jErrObject.addProperty(Const.MESSAGE, Const.SUCCESS_MESSAGE);
		jErrObject.addProperty(Const.STACK_TRACE, "");

		return jErrObject;
	}
}
