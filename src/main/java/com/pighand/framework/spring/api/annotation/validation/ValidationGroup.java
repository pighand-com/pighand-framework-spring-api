package com.pighand.framework.spring.api.annotation.validation;

import jakarta.validation.groups.Default;

/**
 * @Validated group
 *
 * @author wangshuli
 */
public interface ValidationGroup {

    interface Create {}

    interface Update {}

    interface Delete {}

    interface Find {}

    interface Query {}

    interface List {}

    interface Page {}

    interface Custom {}

    interface CreateDefault extends Default {}

    interface UpdateDefault extends Default {}

    interface DeleteDefault extends Default {}

    interface FindDefault extends Default {}

    interface QueryDefault extends Default {}

    interface ListDefault extends Default {}

    interface PageDefault extends Default {}

    interface CustomDefault extends Default {}
}
