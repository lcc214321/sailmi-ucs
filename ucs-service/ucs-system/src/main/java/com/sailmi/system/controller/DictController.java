/**
 * Copyright (c) 2018-2028.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sailmi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.sailmi.core.secure.AuthUser;
import com.sailmi.system.entity.Enterprise;
import com.sailmi.system.feign.IEnterpriseFeign;
import com.sailmi.system.vo.DictVO;
import com.sailmi.system.vo.EnterpriseVO;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import com.sailmi.core.boot.ctrl.AppController;
import com.sailmi.core.mp.support.Condition;
import com.sailmi.core.tool.api.R;
import com.sailmi.core.tool.node.INode;
import com.sailmi.core.tool.utils.Func;
import com.sailmi.system.entity.Dict;
import com.sailmi.system.service.IDictService;
import com.sailmi.system.wrapper.DictWrapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

import static com.sailmi.common.cache.CacheNames.DICT_LIST;
import static com.sailmi.common.cache.CacheNames.DICT_VALUE;

/**
 * 控制器
 *
 * @author Chill
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@Api(value = "字典", tags = "字典管理接口")
public class DictController extends AppController {

	private IDictService dictService;
	private IEnterpriseFeign iEnterpriseFeign;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入dict")
	public R<DictVO> detail(Dict dict) {
		Dict detail = dictService.getOne(Condition.getQueryWrapper(dict));
		return R.data(DictWrapper.build().entityVO(detail));
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "code", value = "字典编号", paramType = "query", dataType = "string"),
		@ApiImplicitParam(name = "dictValue", value = "字典名称", paramType = "query", dataType = "string")
	})
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "列表", notes = "传入dict")
	public R<List<INode>> list(AuthUser authUser, @ApiIgnore @RequestParam Map<String, Object> dict) {
		//公共字典+企业自己的字典
		QueryWrapper<Dict> queryWrappers = Condition.getQueryWrapper(dict, Dict.class);
		queryWrappers.eq("is_tenant_common",0);
		List<Dict> commondicList = dictService.list(queryWrappers);


		QueryWrapper<Dict> queryWrapper = Condition.getQueryWrapper(dict, Dict.class);
		if(authUser!=null && authUser.getEnterpriseId()!=null){
			Long aLong = Long.valueOf(authUser.getEnterpriseId());
			Enterprise enterprise = new Enterprise();
			enterprise.setId(aLong);
			R<EnterpriseVO> enterpriseVOR = iEnterpriseFeign.detailInfo(enterprise);
			if(enterpriseVOR!=null && enterpriseVOR.getData()!=null && enterpriseVOR.getData().getTenantId()!=null){
				queryWrapper.eq("tenant_id",enterpriseVOR.getData().getTenantId());
			}
		}
		@SuppressWarnings("unchecked")
		List<Dict> tenantlist = dictService.list(queryWrapper.lambda().orderByAsc(Dict::getSort));

		Set<Dict> dicts = new HashSet<Dict>();
		dicts.addAll(commondicList);
		dicts.addAll(tenantlist);
		List<Dict> list = new ArrayList<>(dicts);
		return R.data(DictWrapper.build().listNodeVO(list));
	}

	/**
	 * 获取字典树形结构
	 *
	 * @return
	 */
	@GetMapping("/tree")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "树形结构", notes = "树形结构")
	public R<List<DictVO>> tree() {
		List<DictVO> tree = dictService.tree();
		return R.data(tree);
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增或修改", notes = "传入dict")
	public R submit(@Valid @RequestBody Dict dict) {
		return R.status(dictService.submit(dict));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@CacheEvict(cacheNames = {DICT_LIST, DICT_VALUE}, allEntries = true)
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(dictService.removeByIds(Func.toLongList(ids)));
	}

	/**
	 * 获取字典
	 *
	 * @return
	 */
	@GetMapping("/dictionary")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "获取字典", notes = "获取字典")
	public R<List<Dict>> dictionary(String code) {
		List<Dict> tree = dictService.getList(code);
		return R.data(tree);
	}


}
