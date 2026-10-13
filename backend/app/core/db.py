import json
import os
import logging
from typing import Any
import aiofiles


class JsonEngine:
    def __init__(self, file_path: str) -> None:
        self._file_path: str = file_path
        self._data: dict[str, Any] = {}
        self._pk_names: dict[str, str] = {}
        self._dirty: bool = False

    async def _file_exists(self) -> None:
        if os.path.exists(self._file_path):
            return
        async with aiofiles.open(self._file_path, mode="w", encoding="utf-8") as f:
            await f.write(json.dumps({}))

    async def load(self) -> dict[str, Any]:
        await self._file_exists()

        async with aiofiles.open(self._file_path, mode="r", encoding="utf-8") as f:
            content = await f.read()
            try:
                self._data = json.loads(content) if content else {}
            except json.JSONDecodeError:
                logging.error("Error read json in file!")
                self._data = {}

        self._dirty = False
        return self._data

    async def write(self, data: dict[str, Any]) -> None:
        await self._file_exists()
        self._data = data
        async with aiofiles.open(self._file_path, mode="w", encoding="utf-8") as f:
            await f.write(json.dumps(self._data, ensure_ascii=False))

        self._dirty = False

    def get_data(self) -> dict[str, Any]:
        return self._data

    def set_dirty(self, state: bool) -> None:
        self._dirty = state

    def is_dirty(self) -> bool:
        return self._dirty


engine = JsonEngine("backend/data.json")
