/*
Copyright (c) REBUILD <https://getrebuild.com/> and its owners. All rights reserved.

rebuild is dual-licensed under commercial and open source licenses (GPLv3).
See LICENSE and COMMERCIAL in the project root for license information.
*/

package com.rebuild.server.configuration;

import cn.devezhao.persist4j.engine.ID;
import com.alibaba.fastjson.JSON;
import com.rebuild.server.Application;
import com.rebuild.server.metadata.EntityHelper;
import com.rebuild.server.service.bizz.UserHelper;
import com.rebuild.utils.JSONUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 项目管理
 *
 * @author devezhao
 * @since 2020/6/29
 */
public class ProjectManager implements ConfigManager {

    public static final ProjectManager instance = new ProjectManager();
    private ProjectManager() { }

    /**
     * 获取指定用户可用项目
     *
     * @param user
     * @return
     */
    public ConfigEntry[] getAvailable(ID user) {
        final String ckey = "ProjectManager";
        ConfigEntry[] cache = (ConfigEntry[]) Application.getCommonCache().getx(ckey);

        if (cache == null) {
            Object[][] array = Application.createQueryNoFilter(
                    "select configId,projectCode,projectName,principal,members,showConfig from ProjectConfig")
                    .array();

            List<ConfigEntry> alist = new ArrayList<>();
            for (Object[] o : array) {
                ConfigEntry e = new ConfigEntry()
                        .set("id", o[0])
                        .set("projectCode", o[1])
                        .set("projectName", o[2])
                        .set("principal", o[3]);

                String members = (String) o[4];
                if (members.length() >= 20) {
                    Set<ID> users = UserHelper.parseUsers(Arrays.asList(members.split(",")), null);
                    e.set("members", users);
                }

                String showConfig = (String) o[5];
                if (JSONUtils.wellFormat(showConfig)) {
                    e.set("showConfig", JSON.parseObject(showConfig));
                }
            }

            cache = alist.toArray(new ConfigEntry[0]);
            Application.getCommonCache().putx(ckey, cache);
        }

        List<ConfigEntry> alist = new ArrayList<>();
        for (ConfigEntry e : cache) {
            Set<ID> members = e.get("members", Set.class);
            if (members == null || members.contains(user)) {
                alist.add(e.clone());
            }
        }
        return alist.toArray(new ConfigEntry[0]);
    }

    /**
     * 获取项目面板
     *
     * @param projectId
     * @return
     */
    public ConfigEntry[] getPlanList(ID projectId) {
        final String ckey = "ProjectPlan-" + projectId;
        ConfigEntry[] cache = (ConfigEntry[]) Application.getCommonCache().getx(ckey);

        if (cache == null) {
            Object[][] array = Application.createQueryNoFilter(
                    "select configId,planName from ProjectPlanConfig where projectId = ? order seq asc")
                    .setParameter(1, projectId)
                    .array();

            List<ConfigEntry> alist = new ArrayList<>();
            for (Object[] o : array) {
                ConfigEntry e = new ConfigEntry()
                        .set("id", o[0])
                        .set("planName", o[1]);
            }

            cache = alist.toArray(new ConfigEntry[0]);
            Application.getCommonCache().putx(ckey, cache);
        }

        return cache.clone();
    }

    @Override
    public void clean(Object nullOrProjectId) {
        if (nullOrProjectId == null) {
            Application.getCommonCache().evict("ProjectManager");
        } else if (((ID) nullOrProjectId).getEntityCode() == EntityHelper.ProjectConfig) {
            Application.getCommonCache().evict("ProjectPlan-" + nullOrProjectId);
        }
    }
}
