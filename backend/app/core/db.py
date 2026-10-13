import json
import os
import logging
from typing import Any
import aiofiles


class JsonEngine:
    def __init__(self, file_path: str) -> None:
        self.file_path: str = file_path
        self.data: dict[str, Any] = {}
        self.pk_names: dict[str, str] = {}
        self.dirty: bool = False

    async def _file_exists(self) -> None:
        if os.path.exists(self.file_path):
            return
        async with aiofiles.open(self.file_path, mode='w', encoding='utf-8') as f:
            await f.write(json.dumps({}))

    async def create_db(self, name: str) -> None:
        await self._file_exists()

        async with aiofiles.open(self.file_path, mode='r', encoding='utf-8') as f:
            content = await f.read()
            try:
                self.data = json.loads(content) if content else {}
            except json.JSONDecodeError:
                self.data = {}

        if self.data.get(name) is None:
            self.data[name] = {}
            self.dirty = True
            await self.write()

    async def read(self) -> dict[str, Any]:
        if self.dirty:
            await self.write()

        await self._file_exists()
        async with aiofiles.open(self.file_path, mode='r', encoding='utf-8') as f:
            content = await f.read()
            try:
                self.data = json.loads(content) if content else {}
            except json.JSONDecodeError:
                logging.error('Error read json in file!')
                self.data = {}

        self.dirty = False
        return self.data

    async def write(self) -> None:
        if not self.dirty and os.path.exists(self.file_path):
            return

        await self._file_exists()
        async with aiofiles.open(self.file_path, mode='w', encoding='utf-8') as f:
            await f.write(json.dumps(self.data, ensure_ascii=False))

        self.dirty = False

    async def create_item(self, db_name: str, item: dict, pk_name: str | None = None) -> None:
        if db_name not in self.data:
            await self.create_db(db_name)

        if pk_name:
            if self.pk_names.get(db_name) and pk_name != self.pk_names.get(db_name):
                raise ValueError(f'pk name different! In db: {self.pk_names.get(db_name)}')
            self.pk_names[db_name] = pk_name
        else:
            pk_name = self.pk_names[db_name]

        if item.get(pk_name) is None:
            raise ValueError(f'pk key in item not exist! item: {item} key: {pk_name}')
        if self.data[db_name].get(item.get(pk_name)):
            raise KeyError(f'Key {item.get(pk_name)} already exist!')
        item.pop(pk_name)
        self.data[db_name][item.get(pk_name)] = item

        self.dirty = True

    async def read_item(self, db_name: str) -> dict:
        await self.read()
        return self.data.get(db_name, {})

    async def update_item(self, db_name: str, pk: Any, new_value: Any) -> bool:
        if db_name not in self.data or pk not in self.data.get(db_name, {}):
            return False

        self.data[db_name][pk] = new_value
        self.dirty = True
        return True

    async def delete_item(self, db_name: str, pk: Any) -> bool:
        if db_name not in self.data or pk not in self.data.get(db_name, {}):
            return False

        self.data[db_name].pop(pk)
        self.dirty = True
        return True


engine = JsonEngine("backend/data.json")
