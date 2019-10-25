package com.xxl.job.executor.Models;

import cn.hutool.core.clone.CloneSupport;
import lombok.Data;

@Data
public class Company extends CloneSupport<Company> {
    KeyValueModel company_name;
    KeyValueModel company_code;
    KeyValueModel company_leaders;
}
