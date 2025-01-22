@FilterDef(
        name = "nonDeletedEntityFilter",
        parameters = @ParamDef(name = "isDeleted", type = Boolean.class)
)

package com.chat_mat_rest_service.entities;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;