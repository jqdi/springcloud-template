package com.company.user.api.interfaces;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface WalletIncomeUseRecordApi {

	@GetMapping("/selectId4Expire")
	List<Integer> selectId4Expire(@RequestParam("limit") Integer limit);

	@PostMapping("/update4Expire")
	Boolean update4Expire(@RequestParam("id") Integer id);
}